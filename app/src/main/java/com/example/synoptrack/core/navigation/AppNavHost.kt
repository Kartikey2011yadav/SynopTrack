package com.example.synoptrack.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.synoptrack.auth.presentation.LoginScreen
import com.example.synoptrack.auth.presentation.PermissionIntroScreen
import com.example.synoptrack.auth.presentation.PermissionLocationScreen
import com.example.synoptrack.auth.presentation.PermissionNotificationScreen
import com.example.synoptrack.auth.presentation.SplashScreen
import com.example.synoptrack.mapos.presentation.MapOSScreen
import com.example.synoptrack.profile.presentation.NameSetupScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
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
        composable(Screen.NameSetup.route) {
            NameSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.PermissionIntro.route) {
                        popUpTo(Screen.NameSetup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.PermissionIntro.route) {
            PermissionIntroScreen(
                onContinue = {
                    navController.navigate(Screen.PermissionLocation.route) {
                        popUpTo(Screen.PermissionIntro.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.PermissionLocation.route) {
            PermissionLocationScreen(
                onContinue = {
                    navController.navigate(Screen.PermissionNotification.route) {
                        popUpTo(Screen.PermissionLocation.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.PermissionNotification.route) {
            PermissionNotificationScreen(
                onContinue = {
                    navController.navigate(Screen.MapOS.route) {
                        popUpTo(Screen.PermissionNotification.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.MapOS.route) {
            MapOSScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

