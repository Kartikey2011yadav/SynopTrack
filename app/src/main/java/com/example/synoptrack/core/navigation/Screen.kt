package com.example.synoptrack.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    // Auth & Onboarding
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Permission : Screen("permission")
    object AddPlaces : Screen("add_places")
    object PermissionEducation : Screen("permission_education")
    object NameSetup : Screen("name_setup")

    // Main Tabs
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Discover : Screen("discover", "Discover", Icons.Default.Map)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    
    // Internal

}