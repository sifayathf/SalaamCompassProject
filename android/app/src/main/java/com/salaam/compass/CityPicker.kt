package com.salaam.compass

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.salaam.compass.core.OFFLINE_CITIES
import com.salaam.compass.core.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Choose: current location (default), any city online, or one of the 50 offline cities. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPicker(onPick: (Place?) -> Unit, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Place>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val offline = remember(query) {
        val q = query.trim()
        if (q.isEmpty()) OFFLINE_CITIES
        else OFFLINE_CITIES.filter { it.name.contains(q, true) || it.region.contains(q, true) }
    }

    fun searchOnline() {
        if (query.isBlank() || loading) return
        loading = true
        message = null
        scope.launch {
            try {
                results = withContext(Dispatchers.IO) { CitySearch.search(query) }
                if (results.isEmpty()) message = "No places found online for \"$query\"."
            } catch (e: Exception) {
                results = emptyList()
                message = "Online search unavailable (no internet?). Choose from the offline cities below."
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose location") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("City or town") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { searchOnline() }),
                )
                Button(onClick = { searchOnline() }, enabled = query.isNotBlank() && !loading) {
                    if (loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Filled.Search, "Search online")
                }
            }
            message?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    ListItem(
                        headlineContent = { Text("Current location (default)") },
                        supportingContent = { Text("Uses GPS — works offline") },
                        leadingContent = { Icon(Icons.Filled.LocationOn, null) },
                        modifier = Modifier.clickable { onPick(null) },
                    )
                    HorizontalDivider()
                }
                if (results.isNotEmpty()) {
                    item { SectionHeader("Online results") }
                    items(results) { PlaceRow(it, onPick) }
                }
                item { SectionHeader("Offline cities") }
                items(offline) { PlaceRow(it, onPick) }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun PlaceRow(place: Place, onPick: (Place) -> Unit) {
    ListItem(
        headlineContent = { Text(place.name) },
        supportingContent = { Text(place.region, maxLines = 2) },
        leadingContent = { Icon(Icons.Filled.Place, null) },
        modifier = Modifier.clickable { onPick(place) },
    )
}
