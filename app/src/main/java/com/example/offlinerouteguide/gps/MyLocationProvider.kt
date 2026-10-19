package com.example.offlinerouteguide.gps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.maplibre.compose.location.Location
import org.maplibre.compose.location.LocationProvider

class MyrLocationProvider : LocationProvider {
    private val _location = MutableStateFlow<Location?>(null)

    override val location: StateFlow<Location?> = _location

    fun update(location: Location) {
        _location.value = location
    }
}