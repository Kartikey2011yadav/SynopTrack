package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.theme.ElectricBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) onSetupComplete()
    }

    Scaffold(
        topBar = {
             CenterAlignedTopAppBar(title = { Text("Complete Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                 LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            // Name
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Display Name") },
                leadingIcon = { Icon(Icons.Rounded.Person, null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Email (Only if missing)
            if (uiState.showEmailField) {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Rounded.Email, null) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
            
            // DOB (Simple Text for now, can be DatePicker)
            OutlinedTextField(
                value = uiState.dob,
                onValueChange = viewModel::onDobChanged,
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                leadingIcon = { Icon(Icons.Rounded.CalendarToday, null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = viewModel::submitProfile,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.isValid && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Continue")
            }
        }
    }
}
