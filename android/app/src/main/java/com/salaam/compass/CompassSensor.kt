package com.salaam.compass

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.salaam.compass.core.HeadingFilter
import kotlin.math.abs
import kotlin.math.sqrt

data class CompassReading(
    /** Smoothed heading relative to TRUE north, continuous (may exceed 360). */
    val trueHeading: Double,
    /** Magnetometer status: SensorManager.SENSOR_STATUS_*; null if not reported yet. */
    val accuracy: Int?,
    /** Magnetic field strength is far from what Earth's field should be here (metal, magnets, electronics). */
    val interference: Boolean,
    /** Phone is not held roughly flat; heading is frozen while tilted. */
    val tilted: Boolean,
) {
    /** True only when we trust the reading enough to point at the Qibla. */
    val reliable: Boolean
        get() = !interference && !tilted && accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE
}

/**
 * Reads the device heading. Prefers the fused ROTATION_VECTOR sensor (gyro-assisted, far steadier),
 * falling back to accelerometer + magnetometer. Applies magnetic declination for true north.
 */
class CompassSensor(context: Context, private val onReading: (CompassReading) -> Unit) : SensorEventListener {
    private val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val isAvailable = magSensor != null && (rotationSensor != null || accelSensor != null)

    private val filter = HeadingFilter()
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var accuracy: Int? = null
    private var fieldStrength = 0f
    private var declination = 0f
    private var expectedFieldStrength: Float? = null
    private var lastHeading: Double? = null

    fun setLocation(lat: Double, lon: Double) {
        val field = GeomagneticField(lat.toFloat(), lon.toFloat(), 0f, System.currentTimeMillis())
        declination = field.declination
        expectedFieldStrength = field.fieldStrength / 1000f // nT -> µT
    }

    fun start() {
        if (!isAvailable) return
        val primary = rotationSensor ?: accelSensor
        sm.registerListener(this, primary, SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sm.unregisterListener(this)
        filter.reset()
        lastHeading = null
        hasGravity = false
        hasGeomagnetic = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                publish()
            }
            Sensor.TYPE_ACCELEROMETER -> {
                lowPass(event.values, gravity, hasGravity); hasGravity = true
                if (hasGeomagnetic && SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    publish()
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                val v = event.values
                fieldStrength = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
                accuracy = event.accuracy
                lowPass(v, geomagnetic, hasGeomagnetic); hasGeomagnetic = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) this.accuracy = accuracy
    }

    private fun publish() {
        SensorManager.getOrientation(rotationMatrix, orientation)
        val pitch = Math.toDegrees(orientation[1].toDouble())
        val roll = Math.toDegrees(orientation[2].toDouble())
        // Azimuth becomes unstable as the phone approaches vertical, so hold the last heading instead of spinning.
        val tilted = abs(pitch) > MAX_TILT_DEG || abs(roll) > MAX_TILT_DEG
        if (!tilted || lastHeading == null) {
            val magnetic = Math.toDegrees(orientation[0].toDouble())
            lastHeading = filter.update(magnetic + declination)
        }
        val expected = expectedFieldStrength
        val interference = if (expected != null) {
            abs(fieldStrength - expected) / expected > MAX_FIELD_DEVIATION
        } else {
            fieldStrength !in EARTH_FIELD_MIN_UT..EARTH_FIELD_MAX_UT
        }
        onReading(CompassReading(lastHeading!!, accuracy, interference, tilted))
    }

    private fun lowPass(input: FloatArray, output: FloatArray, initialized: Boolean) {
        for (i in 0..2) output[i] = if (initialized) output[i] + LOW_PASS_ALPHA * (input[i] - output[i]) else input[i]
    }

    private companion object {
        const val MAX_TILT_DEG = 45.0
        const val MAX_FIELD_DEVIATION = 0.3f
        const val EARTH_FIELD_MIN_UT = 20f
        const val EARTH_FIELD_MAX_UT = 70f
        const val LOW_PASS_ALPHA = 0.2f
    }
}
