package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.synoptrack.auth.presentation.model.ProfileSetupState
import com.example.synoptrack.core.theme.ElectricBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) onSetupComplete()
    }
    
    // Date Picker Logic
    val calendar = java.util.Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            viewModel.onDobChanged(formattedDate)
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )
    
    ProfileSetupScreenContent(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onBioChanged = viewModel::onBioChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPhoneChanged = viewModel::onPhoneChanged,
        onDobClick = { datePickerDialog.show() },
        onSubmit = viewModel::submitProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreenContent(
    uiState: ProfileSetupState,
    onNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onDobClick: () -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Black,
        topBar = {
             CenterAlignedTopAppBar(
                 title = { Text("Complete Profile", color = androidx.compose.ui.graphics.Color.White) },
                 colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Black)
             )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (uiState.isLoading) {
                 LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = ElectricBluePrimary)
            }
            if (uiState.error != null) {
                Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            if (uiState.message != null) {
                Text(uiState.message!!, color = ElectricBluePrimary, style = MaterialTheme.typography.bodySmall)
            }
            
            // Name
            Column {
                Text("Display Name", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = onNameChanged,
                    placeholder = { Text("Enter your name") },
                    leadingIcon = { Icon(Icons.Rounded.Person, null, tint = androidx.compose.ui.graphics.Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    singleLine = true
                )
            }
            
            // Bio
             Column {
                Text("Bio", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.bio,
                    onValueChange = onBioChanged,
                    placeholder = { Text("Tell us about yourself") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    minLines = 3
                )
            }

            // Email
             Column {
                Text("Email Address", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChanged,
                    placeholder = { Text("Enter your email") },
                    leadingIcon = { Icon(Icons.Rounded.Email, null, tint = androidx.compose.ui.graphics.Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = ElectricBluePrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
            }
            
            // Phone (Optional / Data Collection)
             Column {
                 Text("Phone Number", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.bodyMedium)
                 Spacer(modifier = Modifier.height(8.dp))
                 OutlinedTextField(
                     value = uiState.phoneNumber,
                     onValueChange = onPhoneChanged,
                     placeholder = { Text("934 567 8900") },
                     leadingIcon = { Icon(Icons.Rounded.Phone, null, tint = androidx.compose.ui.graphics.Color.Gray) },
                     modifier = Modifier.fillMaxWidth(),
                     shape = RoundedCornerShape(12.dp),
                     colors = OutlinedTextFieldDefaults.colors(
                         focusedContainerColor = Color(0xFF1E1E1E),
                         unfocusedContainerColor = Color(0xFF1E1E1E),
                         focusedTextColor = Color.White,
                         unfocusedTextColor = Color.White,
                         focusedBorderColor = ElectricBluePrimary,
                         unfocusedBorderColor = Color.Transparent,
                         cursorColor = ElectricBluePrimary
                     ),
                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                     singleLine = true
                 )
            }

            // DOB
            Column {
                Text("Date of Birth", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.dob,
                        onValueChange = {}, // Read only
                        placeholder = { Text("DD/MM/YYYY") },
                        leadingIcon = { Icon(Icons.Rounded.CalendarToday, null, tint = androidx.compose.ui.graphics.Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false, // Disable typing
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                            disabledTextColor = androidx.compose.ui.graphics.Color.White,
                            disabledBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledPlaceholderColor = androidx.compose.ui.graphics.Color.Gray,
                            disabledLeadingIconColor = androidx.compose.ui.graphics.Color.Gray
                        )
                    )
                    // Transparent clickable surface over the text field
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { onDobClick() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.isValid && !uiState.isLoading,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBluePrimary,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Gray
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Complete Setup", color = androidx.compose.ui.graphics.Color.Black, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfileSetupScreenPreview() {
    ProfileSetupScreenContent(
        uiState = ProfileSetupState(
            displayName = "John Doe",
            bio = "Some bio here",
            email = "john@example.com",
            dob = "01/01/2000"
        ),
        onNameChanged = {},
        onBioChanged = {},
        onEmailChanged = {},
        onPhoneChanged = {},
        onDobClick = {},
        onSubmit = {}
    )
}
