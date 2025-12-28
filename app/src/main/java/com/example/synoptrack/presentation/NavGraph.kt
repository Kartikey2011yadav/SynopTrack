package com.example.synoptrack.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.synoptrack.domain.repository.AuthRepository
import com.example.synoptrack.presentation.home.HomeScreen
import com.example.synoptrack.presentation.login.LoginScreen
import com.example.synoptrack.presentation.profile.ProfileSettingsScreen
import com.example.synoptrack.presentation.splash.SplashScreen

@Composable
fun NavGraph(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val startDestination = if (authRepository.currentUser == null) Screen.Login.route else Screen.Home.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    // For now, just navigate back to login.
                    // In a real app, you'd call authRepository.signOut() here or in a ViewModel.
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
