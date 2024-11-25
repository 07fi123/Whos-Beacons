package com.example.whosbeacons.model

// CellTowerModels.kt
data class CellTowerResponse(
    val count: Int,
    val cells: List<CellTower>
)

data class CellTower(
    val lat: Double,
    val lon: Double,
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val cellid: Long,
    val averageSignalStrength: Int,
    val range: Int,
    val samples: Int,
    val changeable: Int,
    val radio: String,
    val rnc: Int,
    val cid: Int,
    val tac: Int,
    val sid: Int,
    val nid: Int,
    val bid: Int
)