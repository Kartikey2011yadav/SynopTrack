package com.example.synoptrack.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.synoptrack.domain.repository.AuthRepository
import com.example.synoptrack.presentation.home.HomeScreen
import com.example.synoptrack.presentation.login.LoginScreen
import com.example.synoptrack.presentation.splash.SplashScreen
import javax.inject.Inject

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
            HomeScreen()
        }
    }
}
