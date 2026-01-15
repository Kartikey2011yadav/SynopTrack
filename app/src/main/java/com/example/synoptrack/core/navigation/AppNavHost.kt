package com.example.synoptrack.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            
            composable(Screen.Welcome.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                com.example.synoptrack.auth.presentation.WelcomeScreen(
                    onLogin = { navController.navigate(Screen.Login.route) },
                    onCreateAccount = { navController.navigate(Screen.SignUp.route) },
                    onTermsClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://synoptrack.com/terms"))
                        context.startActivity(intent)
                    }
                )
            }
            
            composable(Screen.Login.route) {
                com.example.synoptrack.auth.presentation.LoginScreen(
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    // onNavigateToPhone Removed
                    onNavigateToGoogle = { /* Google Login Logic */ },
                    onNavigateToProfileSetup = {
                        navController.navigate(Screen.ProfileSetup.route) {
                             popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToPermission = {
                        navController.navigate(Screen.Permission.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.SignUp.route) {
                com.example.synoptrack.auth.presentation.SignUpScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    // onNavigateToPhone Removed
                    onNavigateToGoogle = { /* Google Login Logic */ },
                    onNavigateToProfileSetup = {
                        navController.navigate(Screen.ProfileSetup.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToPermission = {
                        navController.navigate(Screen.Permission.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ForgotPassword.route) {
                com.example.synoptrack.auth.presentation.ForgotPasswordScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // PhoneLogin removed
            

            composable(Screen.ProfileSetup.route) {
                com.example.synoptrack.auth.presentation.ProfileSetupScreen(
                    onSetupComplete = {
                        navController.navigate(Screen.Permission.route) {
                            popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // ONBOARDING FLOW
            composable(Screen.Permission.route) {
                PermissionScreen(
                    onPermissionGranted = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            // MAIN BOTTOM NAV TABS
            composable(Screen.Home.route) {
                MapOSScreen(
                    onActivityClick = { navController.navigate(Screen.Activity.route) }
                )
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

            composable(Screen.Activity.route) {
                com.example.synoptrack.social.presentation.ActivityScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onEditProfile = { navController.navigate(Screen.EditProfile.route) }
                )
            }
            composable(Screen.Settings.route) {
                com.example.synoptrack.profile.presentation.SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.EditProfile.route) {
                val viewModel: com.example.synoptrack.profile.presentation.ProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                if (uiState.user != null) {
                    com.example.synoptrack.profile.presentation.EditProfileScreen(
                        currentName = uiState.user!!.displayName,
                        currentDiscriminator = uiState.user!!.discriminator,
                        currentBio = uiState.user!!.bio,
                        onBackClick = { navController.popBackStack() },
                        onSaveClick = { name, hash, bio ->
                            viewModel.updateIdentity(name, hash, bio)
                            navController.popBackStack()
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         androidx.compose.material3.CircularProgressIndicator()
                    }
                }
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
