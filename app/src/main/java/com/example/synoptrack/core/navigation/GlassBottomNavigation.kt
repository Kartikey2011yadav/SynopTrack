package com.example.synoptrack.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Home,
        Screen.Social,
        Screen.Search,
        Screen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isVisible = currentRoute in items.map { it.route }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface, // Solid background
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon!!,
                            contentDescription = screen.title
                        )
                    },
                    label = { 
                         // Instagram doesn't typically show labels, but Material3 does.
                         // Let's hide them for cleaner look or keep them if requested.
                         // User said "Instagram like", Insta hides labels usually.
                         // Let's hide labels for now.
                    },
                    selected = isSelected,
                    alwaysShowLabel = false, 
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.surface // Remove pill indicator background
                    )
                )
            }
        }
    }
}
