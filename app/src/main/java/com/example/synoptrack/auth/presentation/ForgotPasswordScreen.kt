package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.core.presentation.components.SynopTrackTextField
import com.example.synoptrack.core.theme.ElectricBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val signInState by viewModel.signInState.collectAsState()
    
    var email by remember { mutableStateOf("") }

    // Side effects now handled by Global Toast Service in ViewModel
    LaunchedEffect(signInState) {
        if (signInState is SignInState.MessageSent) {
            onBack() // Go back to login after sending
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
             CenterAlignedTopAppBar(
                 title = { Text("Reset Password", color = Color.White) },
                 navigationIcon = {
                     IconButton(onClick = onBack) {
                         Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
                     }
                 },
                 colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
             )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
             Text(
                text = "Enter your email address to get the password reset link.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email Input
            SynopTrackTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "youremail@gmail.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val isLoading = signInState is SignInState.Loading
            SynopTrackButton(
                text = "Send Link",
                onClick = { viewModel.resetPassword(email) },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
