package com.example.whosbeacons

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.whosbeacons.screens.CellTowersScreen
import com.example.whosbeacons.ui.theme.WhosbeaconsTheme
import com.example.whosbeacons.viewModel.CellTowersViewModel
import org.ramani.compose.LocationRequestProperties
import com.example.whosbeacons.network.LocationManager
import com.example.whosbeacons.screens.CellTowerMapScreen
import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle


data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val items = listOf(
    BottomNavItem(Screen.Map.route, Icons.Filled.Home, "Home"),
    BottomNavItem(Screen.Towers.route, Icons.Filled.Person, "Profile"),
    BottomNavItem(Screen.Settings.route, Icons.Filled.Settings, "Settings")
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val locationManager by lazy { LocationManager(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPropertiesState: MutableState<LocationRequestProperties?> = mutableStateOf(null)
        requestPermissions(locationPropertiesState)
        MapLibre.getInstance(this)




       // enableEdgeToEdge()
        setContent {
            WhosbeaconsTheme {

            val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry = navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry.value?.destination

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.label) },
                                    label = { Text(screen.label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Restore state when reselecting a previously selected item
                                            launchSingleTop = true
                                            // Restore state even when this destination is not the current destination
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding -> NavHost(
                        navController = navController,
                        startDestination = Screen.Map.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Map.route) { CellTowerMapScreen(
                            viewModel = CellTowersViewModel(
                                locationManager = locationManager
                            ),
                            locationPropertiesState = locationPropertiesState,
                        )}
                        composable(Screen.Towers.route) {
                            CellTowersScreen(
                            viewModel = CellTowersViewModel(
                                locationManager = locationManager
                            ),

                            ) }
                        composable(Screen.Settings.route) {     Text("Settings Screen")
                        }
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



