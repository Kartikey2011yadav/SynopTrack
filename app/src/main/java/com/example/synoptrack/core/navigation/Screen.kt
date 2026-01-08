package com.example.synoptrack.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val selectedIcon: ImageVector? = null, val unselectedIcon: ImageVector? = null) {
    val icon: ImageVector? get() = selectedIcon ?: unselectedIcon

    // Auth & Onboarding
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Permission : Screen("permission")
    object AddPlaces : Screen("add_places")
    object PermissionEducation : Screen("permission_education")
    object NameSetup : Screen("name_setup")

    // Main Tabs
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Social : Screen("social", "Connect", Icons.Filled.Chat, Icons.Outlined.Chat) // Or specific Chat alternatives
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    
    // Internal
    object Chat : Screen("chat/{groupId}") {
        fun createRoute(groupId: String) = "chat/$groupId"
    }
    object Settings : Screen("settings")
}