package com.example.synoptrack.auth.presentation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.theme.ElectricBluePrimary
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneLoginScreen(
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToPermission: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val signInState by viewModel.signInState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Country Code State
    var countryCode by remember { mutableStateOf("+1") }
    var expanded by remember { mutableStateOf(false) }
    val countryCodes = listOf("+1" to "US", "+91" to "IN", "+44" to "UK", "+81" to "JP", "+86" to "CN")
    
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    LaunchedEffect(signInState) {
        when (val state = signInState) {
            is SignInState.OtpSent -> {
                isOtpSent = true
                Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show()
            }
            is SignInState.Success -> {
                // Navigation handled by event
            }
            is SignInState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }
    
    // Listen for Navigation Events
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AuthNavigationEvent.NavigateToCompleteProfile -> onNavigateToProfileSetup()
                is AuthNavigationEvent.NavigateToPermissionCheck -> onNavigateToPermission()
            }
        }
    }

    Scaffold(
        topBar = { 
            CenterAlignedTopAppBar(
                title = { Text("Phone Login", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            ) 
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (signInState is SignInState.Loading) {
                CircularProgressIndicator(color = ElectricBluePrimary)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!isOtpSent) {
                // Phone Input Step
                Text("Enter your mobile number", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Country Code Picker
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.width(100.dp).height(56.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text(countryCode)
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            countryCodes.forEach { (code, country) ->
                                DropdownMenuItem(
                                    text = { Text("$country ($code)") },
                                    onClick = {
                                        countryCode = code
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Rounded.Phone, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBluePrimary,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = ElectricBluePrimary,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = ElectricBluePrimary
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { 
                        if (activity != null && phoneNumber.isNotBlank()) {
                            val fullNumber = "$countryCode$phoneNumber"
                            viewModel.startPhoneAuth(activity, fullNumber)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("Send Code", fontWeight = FontWeight.SemiBold)
                }
            } else {
                // OTP Input Step
                Text("Enter the 6-digit code sent to $phoneNumber", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it },
                    label = { Text("OTP Code") },
                    leadingIcon = { Icon(Icons.Rounded.Sms, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                     shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                     colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBluePrimary,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = ElectricBluePrimary,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = ElectricBluePrimary
                        )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.verifyOtp(otpCode) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = otpCode.length == 6,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("Verify", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
