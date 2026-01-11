package com.example.synoptrack.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
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
    object Home : Screen("home", "Home", Icons.Rounded.Home, Icons.Outlined.Home)
    object Social : Screen("social", "Connect", Icons.Rounded.Forum, Icons.Outlined.Forum) 
    object Search : Screen("search", "Search", Icons.Rounded.Search, Icons.Outlined.Search)
    object Profile : Screen("profile", "Profile", Icons.Rounded.AccountCircle, Icons.Outlined.AccountCircle)
    
    // Internal
    object Chat : Screen("chat/{groupId}") {
        fun createRoute(groupId: String) = "chat/$groupId"
    }
    object Settings : Screen("settings")
}