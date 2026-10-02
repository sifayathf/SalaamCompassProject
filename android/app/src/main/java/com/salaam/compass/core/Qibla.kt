package com.salaam.compass.core

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Great-circle Qibla calculations. Pure math, works fully offline. */
object Qibla {
    const val KAABA_LAT = 21.4224779
    const val KAABA_LON = 39.8251832
    private const val EARTH_RADIUS_KM = 6371.0088

    /** Initial great-circle bearing from (lat, lon) to the Kaaba, in degrees clockwise from true north [0, 360). */
    fun bearing(lat: Double, lon: Double): Double {
        val phi1 = Math.toRadians(lat)
        val phi2 = Math.toRadians(KAABA_LAT)
        val dLon = Math.toRadians(KAABA_LON - lon)
        val y = sin(dLon) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLon)
        return normalize(Math.toDegrees(atan2(y, x)))
    }

    /** Haversine distance to the Kaaba in kilometres. */
    fun distanceKm(lat: Double, lon: Double): Double {
        val phi1 = Math.toRadians(lat)
        val phi2 = Math.toRadians(KAABA_LAT)
        val dPhi = phi2 - phi1
        val dLon = Math.toRadians(KAABA_LON - lon)
        val a = sin(dPhi / 2) * sin(dPhi / 2) + cos(phi1) * cos(phi2) * sin(dLon / 2) * sin(dLon / 2)
        return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
    }
}

/** Maps any angle to [0, 360). */
fun normalize(deg: Double): Double = ((deg % 360.0) + 360.0) % 360.0

/** Signed shortest rotation from [from] to [to], in (-180, 180]. */
fun angleDiff(to: Double, from: Double): Double {
    val d = normalize(to - from)
    return if (d > 180.0) d - 360.0 else d
}
