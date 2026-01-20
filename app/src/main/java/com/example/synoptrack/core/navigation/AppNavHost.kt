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
import com.example.synoptrack.auth.presentation.LoginScreen
import com.example.synoptrack.auth.presentation.PermissionScreen
import com.example.synoptrack.auth.presentation.SplashScreen
import com.example.synoptrack.profile.presentation.NameSetupScreen
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.synoptrack.mapos.presentation.MapOSScreen
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
                val context = androidx.compose.ui.platform.LocalContext.current
                com.example.synoptrack.auth.presentation.LoginScreen(
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    // onNavigateToPhone Removed
                    onNavigateToGoogle = { /* Google Login Logic */ },
                    onNavigateToNameSetup = {
                        navController.navigate(Screen.NameSetup.route) {
                             popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToProfileSetup = {
                        navController.navigate(Screen.ProfileSetup.route) {
                             popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToPermission = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        
                        if (hasPermission) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Permission.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.SignUp.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                com.example.synoptrack.auth.presentation.SignUpScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    // onNavigateToPhone Removed
                    onNavigateToGoogle = { /* Google Login Logic */ },
                     onNavigateToNameSetup = {
                        navController.navigate(Screen.NameSetup.route) {
                             popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToProfileSetup = {
                        navController.navigate(Screen.ProfileSetup.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToPermission = {
                         val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        
                        if (hasPermission) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Permission.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
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
            

            composable(Screen.NameSetup.route) {
                val viewModel: com.example.synoptrack.auth.presentation.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val signInState by viewModel.signInState.collectAsState()
                
                // Listen for navigation events from saveIdentity
                androidx.compose.runtime.LaunchedEffect(key1 = true) {
                    viewModel.navigationEvent.collect { event ->
                       if (event is com.example.synoptrack.auth.presentation.AuthNavigationEvent.NavigateToCompleteProfile) {
                           navController.navigate(Screen.ProfileSetup.route) {
                               popUpTo(Screen.NameSetup.route) { inclusive = true }
                           }
                       }
                    }
                }

                NameSetupScreen(
                    onSetupComplete = { name, hash ->
                        viewModel.saveIdentity(name, hash)
                    },
                    checkAvailability = { name, hash ->
                         viewModel.checkIdentityAvailability(name, hash)
                    }
                )
            }

            composable(Screen.ProfileSetup.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                com.example.synoptrack.auth.presentation.ProfileSetupScreen(
                    onSetupComplete = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        
                        if (hasPermission) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Permission.route) {
                                popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                            }
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
                com.example.synoptrack.social.presentation.search.SocialSearchScreen(
                    onBack = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                    onShowQr = { navController.navigate(Screen.ShowQr.route) },
                    onScanQr = { navController.navigate(Screen.QrScan.route) }
                )
            }

            composable(Screen.Activity.route) {
                com.example.synoptrack.social.presentation.NotificationScreen(
                    onBack = { navController.popBackStack() }
                )
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
                        },
                        checkAvailability = { name, hash ->
                             viewModel.checkIdentityAvailability(name, hash)
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

            composable(
                route = Screen.Invite.route,
                deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "synoptrack://invite/{code}" }),
                arguments = listOf(androidx.navigation.navArgument("code") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val code = backStackEntry.arguments?.getString("code")
                com.example.synoptrack.social.presentation.invite.InviteHandlerScreen(
                    code = code,
                    onDismiss = {
                        // Go home or back
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.ShowQr.route) {
                val viewModel: com.example.synoptrack.profile.presentation.ProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                if (uiState.user != null) {
                    com.example.synoptrack.social.presentation.qr.UserQrScreen(
                        user = uiState.user!!,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         androidx.compose.material3.CircularProgressIndicator()
                     }
                }
            }

            composable(Screen.QrScan.route) {
                com.example.synoptrack.social.presentation.qr.QrScannerScreen(
                    onBack = { navController.popBackStack() },
                    onCodeScanned = { rawCode ->
                        // Handle Deep Link or Raw Code
                        val code = if (rawCode.startsWith("synoptrack://invite/")) {
                            rawCode.removePrefix("synoptrack://invite/")
                        } else {
                            rawCode
                        }
                        navController.navigate(Screen.Invite.createRoute(code)) {
                            popUpTo(Screen.QrScan.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Standard Bottom Navigation
        AppBottomNavigation(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
