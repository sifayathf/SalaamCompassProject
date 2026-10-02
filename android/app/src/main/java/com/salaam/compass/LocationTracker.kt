package com.salaam.compass

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper

/**
 * Device location via the framework LocationManager (no Google Play Services needed).
 * GPS works without any internet connection.
 */
class LocationTracker(context: Context) {
    private val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var best: Location? = null
    private var listener: LocationListener? = null

    fun isLocationEnabled(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) lm.isLocationEnabled
        else lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    /** Caller must hold a location permission. */
    @SuppressLint("MissingPermission")
    fun start(onFix: (Location) -> Unit) {
        stop()
        val providers = lm.getProviders(true)
        providers.mapNotNull { lm.getLastKnownLocation(it) }.forEach { offer(it, onFix) }

        // Explicit overrides: on API < 30 these methods have no default implementation at runtime.
        val l = object : LocationListener {
            override fun onLocationChanged(location: Location) = offer(location, onFix)
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        listener = l
        for (p in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            if (p in providers) lm.requestLocationUpdates(p, 5_000L, 10f, l, Looper.getMainLooper())
        }
    }

    fun stop() {
        listener?.let { lm.removeUpdates(it) }
        listener = null
    }

    private fun offer(location: Location, onFix: (Location) -> Unit) {
        val current = best
        val isBetter = current == null ||
            location.time - current.time > 2 * 60_000 ||
            location.accuracy <= current.accuracy
        if (isBetter) {
            best = location
            onFix(location)
        }
    }
}
