package com.salaam.compass

import com.salaam.compass.core.Place
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Online search for any city/town worldwide via OpenStreetMap Nominatim. Call off the main thread. */
object CitySearch {
    fun search(query: String): List<Place> {
        val q = URLEncoder.encode(query.trim(), "UTF-8")
        val url = URL("https://nominatim.openstreetmap.org/search?q=$q&format=jsonv2&limit=15&featureType=settlement")
        val conn = url.openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            // Nominatim's usage policy requires an identifying User-Agent.
            conn.setRequestProperty("User-Agent", "SalaamCompass/1.0 (Android)")
            conn.setRequestProperty("Accept-Language", "en")
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val arr = JSONArray(body)
            return (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val display = o.getString("display_name")
                Place(
                    name = o.optString("name").ifBlank { display.substringBefore(",") },
                    region = display,
                    lat = o.getString("lat").toDouble(),
                    lon = o.getString("lon").toDouble(),
                )
            }
        } finally {
            conn.disconnect()
        }
    }
}
