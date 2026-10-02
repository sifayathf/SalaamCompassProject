package com.salaam.compass.core

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Stabilises a noisy compass heading so the dial does not jitter or spin.
 *
 * - Low-pass filters on the unit circle (sin/cos), so 359° -> 1° is a 2° move, not a 358° spin.
 * - A dead band ignores changes smaller than [deadBandDeg].
 * - Output is "unwrapped" (continuous, may exceed 360) so UI animation always takes the short way round.
 */
class HeadingFilter(
    private val alpha: Double = 0.12,
    private val deadBandDeg: Double = 1.0,
) {
    private var s = 0.0
    private var c = 0.0
    private var initialized = false
    private var continuous: Double? = null

    /** Feed a raw heading (degrees); returns the smoothed, continuous heading. */
    fun update(rawDeg: Double): Double {
        val r = Math.toRadians(rawDeg)
        if (!initialized) {
            s = sin(r); c = cos(r); initialized = true
        } else {
            s += alpha * (sin(r) - s)
            c += alpha * (cos(r) - c)
        }
        val smoothed = normalize(Math.toDegrees(atan2(s, c)))
        val last = continuous
        continuous = when {
            last == null -> smoothed
            abs(angleDiff(smoothed, last)) < deadBandDeg -> last
            else -> last + angleDiff(smoothed, last)
        }
        return continuous!!
    }

    fun reset() {
        initialized = false
        continuous = null
    }
}
