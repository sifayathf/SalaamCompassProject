package com.salaam.compass

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.salaam.compass.core.Place
import com.salaam.compass.core.Qibla
import com.salaam.compass.core.angleDiff
import com.salaam.compass.core.normalize
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colors = if (isSystemInDarkTheme()) {
                darkColorScheme(primary = Color(0xFF7FD6A4), secondary = Color(0xFFE8C547))
            } else {
                lightColorScheme(primary = Color(0xFF0B5D3B), secondary = Color(0xFFB8860B))
            }
            MaterialTheme(colorScheme = colors) { SalaamCompassApp() }
        }
    }
}

private const val ALIGNED_TOLERANCE_DEG = 3.0

private fun hasLocationPermission(context: Context) =
    listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

@Composable
fun SalaamCompassApp() {
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val tracker = remember { LocationTracker(context) }

    var selectedCity by remember { mutableStateOf(prefs.loadPlace()) }
    var showPicker by remember { mutableStateOf(false) }
    var fix by remember { mutableStateOf<Location?>(null) }
    var reading by remember { mutableStateOf<CompassReading?>(null) }
    val sensor = remember { CompassSensor(context) { reading = it } }

    var resumed by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(hasLocationPermission(context)) }
    var locationEnabled by remember { mutableStateOf(tracker.isLocationEnabled()) }
    var permissionDenied by remember { mutableStateOf(false) }

    // Re-check permission / location switch whenever the user comes back (e.g. from Settings).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    hasPermission = hasLocationPermission(context)
                    locationEnabled = tracker.isLocationEnabled()
                    resumed = true
                }
                Lifecycle.Event.ON_PAUSE -> resumed = false
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        hasPermission = result.values.any { it }
        permissionDenied = !hasPermission
    }

    val usingCurrentLocation = selectedCity == null

    // Default mode is current location: ask for permission straight away if we don't have it.
    LaunchedEffect(usingCurrentLocation) {
        if (usingCurrentLocation && !hasPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            )
        }
    }

    val trackLocation = resumed && usingCurrentLocation && hasPermission
    DisposableEffect(trackLocation, locationEnabled) {
        if (trackLocation) tracker.start { fix = it }
        onDispose { tracker.stop() }
    }

    DisposableEffect(resumed) {
        if (resumed) sensor.start()
        onDispose { sensor.stop() }
    }

    val coords: Pair<Double, Double>? = selectedCity?.let { it.lat to it.lon } ?: fix?.let { it.latitude to it.longitude }
    LaunchedEffect(coords) { coords?.let { sensor.setLocation(it.first, it.second) } }

    if (showPicker) {
        CityPicker(
            onPick = {
                selectedCity = it
                prefs.savePlace(it)
                showPicker = false
            },
            onBack = { showPicker = false },
        )
        return
    }

    Scaffold { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Salaam Compass", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            AssistChip(
                onClick = { showPicker = true },
                label = { Text(selectedCity?.name ?: "Current location") },
                leadingIcon = {
                    Icon(if (usingCurrentLocation) Icons.Filled.LocationOn else Icons.Filled.Place, null)
                },
            )

            if (usingCurrentLocation) {
                LocationPrompts(
                    hasPermission = hasPermission,
                    permissionDenied = permissionDenied,
                    locationEnabled = locationEnabled,
                    hasFix = fix != null,
                    onRequestPermission = {
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        )
                    },
                    onChooseCity = { showPicker = true },
                )
            }

            if (coords != null) {
                QiblaContent(lat = coords.first, lon = coords.second, reading = reading, sensorAvailable = sensor.isAvailable)
            }
        }
    }
}

@Composable
private fun LocationPrompts(
    hasPermission: Boolean,
    permissionDenied: Boolean,
    locationEnabled: Boolean,
    hasFix: Boolean,
    onRequestPermission: () -> Unit,
    onChooseCity: () -> Unit,
) {
    val context = LocalContext.current
    when {
        !hasPermission -> Notice(
            "Location permission is needed to find the Qibla from where you are. " +
                "It works offline using GPS — no internet needed.",
        ) {
            Button(onClick = onRequestPermission) { Text("Allow location") }
            if (permissionDenied) {
                OutlinedButton(onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
                    )
                }) { Text("Open app settings") }
            }
            OutlinedButton(onClick = onChooseCity) { Text("Choose a city instead") }
        }
        !locationEnabled -> Notice("Location is turned off. Please turn it on to use your current position.") {
            Button(onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }) {
                Text("Turn on location")
            }
            OutlinedButton(onClick = onChooseCity) { Text("Choose a city instead") }
        }
        !hasFix -> Notice("Finding your location… Outdoors or near a window gives a faster GPS fix.") {
            OutlinedButton(onClick = onChooseCity) { Text("Choose a city instead") }
        }
    }
}

@Composable
private fun Notice(text: String, actions: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text, textAlign = TextAlign.Center)
            actions()
        }
    }
}

@Composable
private fun QiblaContent(lat: Double, lon: Double, reading: CompassReading?, sensorAvailable: Boolean) {
    val qibla = remember(lat, lon) { Qibla.bearing(lat, lon) }
    val distance = remember(lat, lon) { Qibla.distanceKm(lat, lon) }
    val haptics = LocalHapticFeedback.current

    val reliable = reading?.reliable == true
    val aligned = reading != null && reliable &&
        abs(angleDiff(qibla, normalize(reading.trueHeading))) <= ALIGNED_TOLERANCE_DEG
    LaunchedEffect(aligned) { if (aligned) haptics.performHapticFeedback(HapticFeedbackType.LongPress) }

    if (sensorAvailable) {
        CompassDial(
            heading = reading?.trueHeading ?: 0.0,
            qibla = qibla,
            reliable = reliable,
            aligned = aligned,
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f),
        )
        SensorStatus(reading, aligned)
    } else {
        Notice("This device has no compass sensor. Face ${qibla.roundToInt()}° from true north using a landmark or a physical compass.") {}
    }

    Spacer(Modifier.height(4.dp))
    Text("Qibla: %.1f° from true North".format(qibla), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    Text("Distance to the Kaaba: %,.0f km".format(distance))
    Text(
        "%.4f, %.4f".format(lat, lon),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SensorStatus(reading: CompassReading?, aligned: Boolean) {
    val (text, color) = when {
        reading == null -> "Starting compass…" to MaterialTheme.colorScheme.onSurfaceVariant
        reading.tilted -> "Hold your phone flat to read the direction." to Color(0xFFE67E22)
        reading.interference -> "Magnetic interference detected. Move away from metal, magnets or electronics." to Color(0xFFE74C3C)
        reading.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE ->
            "Compass needs calibration: wave your phone in a figure-8 a few times." to Color(0xFFE74C3C)
        reading.accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW ->
            "Accuracy is low — a figure-8 motion will improve it." to Color(0xFFE67E22)
        aligned -> "✓ You are facing the Qibla" to Color(0xFF2ECC71)
        else -> "Turn until the gold needle points straight up." to MaterialTheme.colorScheme.onSurface
    }
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth()) {
        Text(text, color = color, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium, modifier = Modifier.padding(12.dp).fillMaxWidth())
    }
}
