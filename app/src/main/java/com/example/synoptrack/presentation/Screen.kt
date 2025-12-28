package com.example.synoptrack.presentation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Login : Screen("login")
    object Profile : Screen("profile")
}