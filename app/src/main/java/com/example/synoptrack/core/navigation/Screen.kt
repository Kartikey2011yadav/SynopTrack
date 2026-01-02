package com.example.synoptrack.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Permission : Screen("permission")
    object MapOS : Screen("map_os")
}