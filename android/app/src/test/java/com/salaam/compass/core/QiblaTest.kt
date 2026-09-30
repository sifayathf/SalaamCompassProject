package com.salaam.compass.core

import org.junit.Assert.assertEquals
import org.junit.Test

class QiblaTest {
    @Test fun knownBearings() {
        assertEquals(58.48, Qibla.bearing(40.7128, -74.0060), 0.05)   // New York
        assertEquals(118.99, Qibla.bearing(51.5074, -0.1278), 0.05)   // London
        assertEquals(295.15, Qibla.bearing(-6.2088, 106.8456), 0.05)  // Jakarta
        assertEquals(277.50, Qibla.bearing(-33.8688, 151.2093), 0.05) // Sydney
    }

    @Test fun knownDistances() {
        assertEquals(10306.0, Qibla.distanceKm(40.7128, -74.0060), 5.0)
        assertEquals(4794.0, Qibla.distanceKm(51.5074, -0.1278), 5.0)
    }

    @Test fun angleHelpers() {
        assertEquals(2.0, angleDiff(1.0, 359.0), 1e-9)
        assertEquals(-2.0, angleDiff(359.0, 1.0), 1e-9)
        assertEquals(350.0, normalize(-10.0), 1e-9)
        assertEquals(10.0, normalize(730.0), 1e-9)
    }
}
