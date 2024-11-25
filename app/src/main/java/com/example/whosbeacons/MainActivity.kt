package com.example.whosbeacons

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.whosbeacons.screens.CellTowersScreen
import com.example.whosbeacons.ui.theme.WhosbeaconsTheme
import com.example.whosbeacons.viewModel.CellTowersViewModel
import org.ramani.compose.LocationRequestProperties
import com.example.whosbeacons.network.LocationManager
import com.example.whosbeacons.screens.CellTowerMapScreen


class MainActivity : ComponentActivity() {

    private val locationManager by lazy { LocationManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPropertiesState: MutableState<LocationRequestProperties?> = mutableStateOf(null)
        requestPermissions(locationPropertiesState)




       // enableEdgeToEdge()
        setContent {

            WhosbeaconsTheme {
//                CellTowerMapScreen(
//                    viewModel = CellTowersViewModel(
//                        locationManager = locationManager
//                    ),
//                    locationPropertiesState = locationPropertiesState,
//                )


                CellTowersScreen(
                    viewModel = CellTowersViewModel(
                        locationManager = locationManager
                    ),

                )
//

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



