package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.R
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.core.presentation.components.SynopTrackTextField
import com.example.synoptrack.core.theme.ElectricBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToGoogle: () -> Unit,
    onNavigateToNameSetup: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToPermission: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val signInState by viewModel.signInState.collectAsState()
    
    // Auth Navigation Listener
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AuthNavigationEvent.NavigateToNameSetup -> onNavigateToNameSetup()
                is AuthNavigationEvent.NavigateToCompleteProfile -> onNavigateToProfileSetup()
                is AuthNavigationEvent.NavigateToPermissionCheck -> onNavigateToPermission()
            }
        }
    }
    
    // Google Sign In Logic
    val context = androidx.compose.ui.platform.LocalContext.current
    val googleSignInClient = remember {
        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
    }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task)
        }
    }

    LoginScreenContent(
        signInState = signInState,
        onSignIn = { email, password -> viewModel.signInWithEmail(email, password) },
        onGoogleSignIn = { launcher.launch(googleSignInClient.signInIntent) },
        onNavigateToSignUp = onNavigateToSignUp,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    signInState: SignInState,
    onSignIn: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onBack: () -> Unit
) {
    // UI State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            SynopTrackTopBar(
                title = "Sign In",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // Header Image
             Image(
                painter = painterResource(id = R.drawable.credentials),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Login Now To Your Account.",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Access your account to manage settings, explore features.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email Input
            SynopTrackTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "youremail@gmail.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Input
            SynopTrackTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "********",
                secure = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Remember Me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = rememberMe,
                        onClick = { rememberMe = !rememberMe },
                        colors = RadioButtonDefaults.colors(selectedColor = ElectricBluePrimary, unselectedColor = Color.Gray)
                    )
                    Text("Remember me", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
                
                Text(
                    text = "Forgot password?",
                    color = ElectricBluePrimary,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onNavigateToForgotPassword() }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Login Button
            val isLoading = signInState is SignInState.Loading
            SynopTrackButton(
                text = "Login",
                onClick = { onSignIn(email, password) },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.weight(1f))
                Text(" OR ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Social Buttons
            SynopTrackButton(
                text = "Sign in with Google",
                onClick = onGoogleSignIn,
                variant = ButtonVariant.OUTLINED,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ", color = Color.Gray)
                Text(
                    text = "Sign Up",
                    color = ElectricBluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        signInState = SignInState.Success(""), // Or Idle
        onSignIn = { _, _ -> },
        onGoogleSignIn = {},
        onNavigateToSignUp = {},
        onNavigateToForgotPassword = {},
        onBack = {}
    )
}
