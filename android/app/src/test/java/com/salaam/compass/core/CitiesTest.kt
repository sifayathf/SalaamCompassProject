package com.salaam.compass.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CitiesTest {
    @Test fun fiftyUniqueValidCities() {
        assertEquals(50, OFFLINE_CITIES.size)
        assertEquals(50, OFFLINE_CITIES.map { it.name }.toSet().size)
        OFFLINE_CITIES.forEach {
            assertTrue(it.name, it.lat in -90.0..90.0 && it.lon in -180.0..180.0)
        }
    }
}
