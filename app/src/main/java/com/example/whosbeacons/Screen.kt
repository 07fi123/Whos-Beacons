package com.example.whosbeacons

sealed class Screen(val route: String) {
    object Map : Screen("Map")
    object Towers : Screen("profile")
    object Settings : Screen("settings")
}