package com.example.whosbeacons.viewModel

import android.location.Location
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whosbeacons.model.CellTower
import com.example.whosbeacons.network.LocationManager
import com.example.whosbeacons.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.maplibre.android.BuildConfig
import kotlin.math.cos

sealed class UiState {
    object Loading : UiState()
    data class Success(val cellTowers: List<CellTower>) : UiState()
    data class Error(val message: String) : UiState()
}

class CellTowersViewModel(
    private val locationManager: LocationManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    init {
        // Start location updates
        viewModelScope.launch {
            locationManager.locationFlow().collect { location ->
                if (location.latitude != _location.value?.latitude || location.longitude != _location.value?.longitude) {
                    _location.value = location
                    fetchCellTowersForLocation(location)
                }
            }
        }
    }

    private suspend fun fetchCellTowersForLocation(location: Location) {
        try {
            _uiState.value = UiState.Loading
            Log.d("WHOSBEACONS", "fetchIngCellTowersForLocation: ")

            // Calculate bounding box around current location (approximately 1km)
            val latOffset = 0.003 // roughly 1km in latitude
            val lonOffset = 0.003 / cos(Math.toRadians(location.latitude))

            val bbox = "${location.latitude - latOffset},${location.longitude - lonOffset}," +
                    "${location.latitude + latOffset},${location.longitude + lonOffset}"


            val response = NetworkModule.apiService.getCellTowers("pk.90a92bc66c60005a53df85dcc45a05a6", bbox)
            _uiState.value = UiState.Success(response.cells)
            Log.d("WHOSBEACONS", "fetchEDCellTowersForLocation: "+response)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun getCellTowerColor(radio: String): Color {
        return when (radio) {
            "LTE" -> Color(0xFF4CAF50)  // Green
            "UMTS" -> Color(0xFF2196F3) // Blue
            "GSM" -> Color(0xFFFFC107)  // Yellow
            else -> Color(0xFF9E9E9E)   // Gray
        }
    }
}
