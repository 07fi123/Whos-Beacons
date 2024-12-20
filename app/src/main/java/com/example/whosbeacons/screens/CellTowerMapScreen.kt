package com.example.whosbeacons.screens
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.whosbeacons.viewModel.CellTowersViewModel
import com.example.whosbeacons.viewModel.UiState
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.CircleWithItem
import org.ramani.compose.LocationRequestProperties
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import org.ramani.compose.MapLibreComposable

@Composable
fun CellTowerMapScreen(
    modifier: Modifier = Modifier,
    viewModel: CellTowersViewModel,
    locationPropertiesState: MutableState<LocationRequestProperties?>,
) {
    val uiState by viewModel.uiState.collectAsState()

    val locationProperties = rememberSaveable { locationPropertiesState }
    val cameraPosition = rememberSaveable { mutableStateOf(CameraPosition()) }
    val userLocation = rememberSaveable { mutableStateOf(Location(null)) }
    val styleUrl = rememberSaveable { mutableStateOf("https://tiles.openfreemap.org/styles/liberty") }
    val styleBuilder = Style.Builder().fromUri(styleUrl.value)


    Box {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            MapView("Map",
                uiState = uiState,
                cameraPosition = cameraPosition.value,
                userLocation = userLocation,
                locationProperties = locationProperties,
                modifier = Modifier,
                styleBuilder = styleBuilder
            )

        }
        Button(
            modifier = Modifier.align(alignment = Alignment.BottomCenter),
            onClick = {
                Log.d("TAG", "Center on device location"+userLocation.value.latitude+userLocation.value.longitude)
                cameraPosition.value = CameraPosition(cameraPosition.value).apply {
                    this.target = LatLng(
                        userLocation.value.latitude,
                        userLocation.value.longitude
                    )
                }
            },
        ) {
            Text(text = "Center on device location")
        }
    }

}

@MapLibreComposable
@Composable
fun Towers(
    uiState: UiState
) {
    when (val state = uiState) {

        is UiState.Success -> {

            state.cellTowers.forEach { tower ->
                val centerLat = tower.lat
                val centerLon = tower.lon
                Log.d("TAG", "Towers: "+tower.radio)
                CircleWithItem(
                    center = LatLng(centerLat,centerLon),
                    text = "Cell Tower - ${tower.radio}",
                    radius = tower.range.toFloat(),
                    color = when (tower.radio) {
                        "LTE" -> "0xFF4CAF50"
                        "UMTS" -> "0xFF2196F3"
                        "GSM" -> "0xFFFFC107"
                        else -> "0xFF9E9E9E"
                    },
                    isDraggable = false,
                    )
            }
        }

        is UiState.Error -> {

        }

        is UiState.Loading -> {

        }
    }
}





@Composable
fun MapView(
    name: String,
    uiState: UiState,
    cameraPosition: CameraPosition,
    userLocation: MutableState<Location>,
    locationProperties: MutableState<LocationRequestProperties?>,
    modifier: Modifier = Modifier,
    styleBuilder: Style.Builder
) {


    // var selectedTower by remember { mutableStateOf<CellTower?>(null) }

    Box(modifier = modifier) {
        MapLibre(
            modifier = Modifier.fillMaxSize(),
            styleBuilder = styleBuilder,
            cameraPosition = cameraPosition,
            locationRequestProperties = locationProperties.value,
            locationStyling = LocationStyling(
                enablePulse = true,
                pulseColor = Color.LightGray.toArgb(),
            ),
            userLocation = userLocation,

        )

    }
}