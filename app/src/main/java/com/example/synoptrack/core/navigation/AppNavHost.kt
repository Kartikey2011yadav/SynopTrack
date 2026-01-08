package com.example.synoptrack.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.synoptrack.auth.presentation.AddPlacesScreen
import com.example.synoptrack.auth.presentation.LoginScreen
import com.example.synoptrack.auth.presentation.PermissionEducationScreen
import com.example.synoptrack.auth.presentation.PermissionScreen
import com.example.synoptrack.auth.presentation.RegistrationScreen
import com.example.synoptrack.auth.presentation.SplashScreen
import com.example.synoptrack.mapos.presentation.MapOSScreen
import com.example.synoptrack.profile.presentation.NameSetupScreen
import com.example.synoptrack.profile.presentation.ProfileScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            // AUTH FLOW
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(navController)
            }
            composable(Screen.Registration.route) {
                RegistrationScreen(
                    onRegistrationComplete = {
                        navController.navigate(Screen.Permission.route) {
                            popUpTo(Screen.Registration.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // ONBOARDING FLOW
            composable(Screen.Permission.route) {
                PermissionScreen(
                    onPermissionGranted = {
                        navController.navigate(Screen.PermissionEducation.route)
                    },
                    onSkip = {
                        navController.navigate(Screen.PermissionEducation.route)
                    }
                )
            }
            composable(Screen.PermissionEducation.route) {
                PermissionEducationScreen(
                    onContinue = {
                        navController.navigate(Screen.AddPlaces.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddPlaces.route) {
                AddPlacesScreen(
                    onNext = {
                        navController.navigate(Screen.NameSetup.route)
                    },
                    onSkip = {
                        navController.navigate(Screen.NameSetup.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.NameSetup.route) {
                NameSetupScreen(
                    onSetupComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Permission.route) { inclusive = true } // Clear onboarding
                        }
                    }
                )
            }

            // MAIN BOTTOM NAV TABS
            composable(Screen.Home.route) {
                MapOSScreen()
            }
            composable(Screen.Social.route) {
                com.example.synoptrack.social.presentation.SocialScreen(
                    onChatClick = { groupId ->
                        navController.navigate(Screen.Chat.createRoute(groupId))
                    }
                )
            }
            composable(Screen.Search.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Search")
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Settings.route) {
                com.example.synoptrack.profile.presentation.SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            // CHAT
            composable(
                route = Screen.Chat.route,
                arguments = listOf(androidx.navigation.navArgument("groupId") { type = androidx.navigation.NavType.StringType })
            ) {
                com.example.synoptrack.social.presentation.chat.ChatScreen(navController)
            }
        }

        // Standard Bottom Navigation
        AppBottomNavigation(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
