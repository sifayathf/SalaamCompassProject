package com.salaam.compass.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class HeadingFilterTest {
    @Test fun wrapAroundNorthDoesNotSpin() {
        val f = HeadingFilter()
        var out = f.update(358.0)
        repeat(100) { out = f.update(2.0) }
        // Continuous output moved +4° (to 362), not -356°.
        assertEquals(362.0, out, 1.0)
    }

    @Test fun smallJitterIsSuppressed() {
        val f = HeadingFilter()
        val first = f.update(90.0)
        repeat(200) { i -> assertEquals(first, f.update(if (i % 2 == 0) 90.8 else 89.2), 1e-9) }
    }

    @Test fun noisySignalIsSmoothed() {
        val f = HeadingFilter()
        f.update(120.0)
        var maxDev = 0.0
        repeat(500) { i ->
            val noisy = 120.0 + if (i % 2 == 0) 8.0 else -8.0
            maxDev = maxOf(maxDev, abs(f.update(noisy) - 120.0))
        }
        assertTrue("max deviation $maxDev", maxDev < 2.0)
    }

    @Test fun followsRealTurn() {
        val f = HeadingFilter()
        f.update(0.0)
        var out = 0.0
        repeat(100) { out = f.update(90.0) }
        assertEquals(90.0, out, 1.0)
    }
}
