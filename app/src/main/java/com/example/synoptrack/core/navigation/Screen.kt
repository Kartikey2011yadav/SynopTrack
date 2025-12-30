package com.example.synoptrack.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object NameSetup : Screen("name_setup")
    object PermissionIntro : Screen("permission_intro")
    object PermissionLocation : Screen("permission_location")
    object PermissionNotification : Screen("permission_notification")
    object MapOS : Screen("map_os")
}