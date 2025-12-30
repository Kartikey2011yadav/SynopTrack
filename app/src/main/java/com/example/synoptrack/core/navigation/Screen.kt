package com.example.synoptrack.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Permissions : Screen("permissions")
    object ProfileSetup : Screen("profile_setup")
    object MapOS : Screen("map_os")
}