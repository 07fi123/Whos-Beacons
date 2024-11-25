package com.example.whosbeacons.screens


import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whosbeacons.model.CellTower
import com.example.whosbeacons.viewModel.CellTowersViewModel
import com.example.whosbeacons.viewModel.UiState
import kotlin.contracts.Effect
import kotlin.math.log


@Composable
fun CellTowersScreen(
    modifier: Modifier = Modifier,
    viewModel: CellTowersViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    val location by viewModel.location.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cell Towers",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.cellTowers) { tower ->
                        CellTowerCard(tower)
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CellTowerCard(tower: CellTower) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Radio: ${tower.radio}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Location: ${tower.lat}, ${tower.lon}")
            Text("MCC: ${tower.mcc}, MNC: ${tower.mnc}")
            Text("Cell ID: ${tower.cellid}")
            Text("Range: ${tower.range}m")
            Text("Signal Strength: ${tower.averageSignalStrength}")
        }
    }
}