package com.salaam.compass

import android.content.Context
import com.salaam.compass.core.Place

/** Remembers the chosen location. null = use current device location (the default). */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("salaam_compass", Context.MODE_PRIVATE)

    fun loadPlace(): Place? {
        val name = sp.getString("name", null) ?: return null
        return Place(
            name = name,
            region = sp.getString("region", "").orEmpty(),
            lat = java.lang.Double.longBitsToDouble(sp.getLong("lat", 0)),
            lon = java.lang.Double.longBitsToDouble(sp.getLong("lon", 0)),
        )
    }

    fun savePlace(place: Place?) {
        sp.edit().apply {
            if (place == null) {
                clear()
            } else {
                putString("name", place.name)
                putString("region", place.region)
                putLong("lat", java.lang.Double.doubleToRawLongBits(place.lat))
                putLong("lon", java.lang.Double.doubleToRawLongBits(place.lon))
            }
        }.apply()
    }
}
