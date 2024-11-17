package com.example.whosbeacons

import android.content.res.Resources.Theme
import android.os.Bundle
import android.Manifest
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.whosbeacons.ui.theme.WhosbeaconsTheme
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationRequestProperties
import org.ramani.compose.MapLibre
import org.ramani.compose.LocationStyling


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPropertiesState: MutableState<LocationRequestProperties?> = mutableStateOf(null)
        requestPermissions(locationPropertiesState)

        enableEdgeToEdge()
        setContent {
            WhosbeaconsTheme {
                val locationProperties = rememberSaveable { locationPropertiesState }
                val cameraPosition = rememberSaveable { mutableStateOf(CameraPosition()) }
                val userLocation = rememberSaveable { mutableStateOf(Location(null)) }

                Box {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MapView("Map",cameraPosition = cameraPosition.value,
                            userLocation = userLocation,
                            locationProperties = locationProperties.value,
                            modifier = Modifier
                        )
                    }
                    Button(
                        modifier = Modifier.align(alignment = Alignment.BottomCenter),
                        onClick = {
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
        }
    }


    private fun requestPermissions(locationPropertiesState: MutableState<LocationRequestProperties?>) {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locationPropertiesState.value = LocationRequestProperties()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    locationPropertiesState.value = LocationRequestProperties()
                }

                else -> {
                    locationPropertiesState.value = null
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}


@Composable
fun MapView(name: String,
            cameraPosition: CameraPosition,
            userLocation: MutableState<Location>,
            locationProperties: LocationRequestProperties?,
            modifier: Modifier = Modifier
) {
    MapLibre(
        modifier = Modifier.fillMaxSize(),
        styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"),
        cameraPosition = cameraPosition,
        locationRequestProperties = locationProperties,
        locationStyling = LocationStyling(
            enablePulse = true,
            pulseColor = Color.Yellow.toArgb(),
        ),
        userLocation = userLocation,
    )
}

