package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.synoptrack.auth.presentation.model.ProfileSetupState
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.core.presentation.components.SynopTrackTextField
import com.example.synoptrack.core.theme.ElectricBluePrimary
import java.util.Calendar

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
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            viewModel.onDobChanged(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
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
        containerColor = Color.Black,
        topBar = {
             CenterAlignedTopAppBar(
                 title = { Text("Complete Profile", color = Color.White) },
                 colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
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
            SynopTrackTextField(
                value = uiState.displayName,
                onValueChange = onNameChanged,
                label = "Display Name",
                placeholder = "Enter your name",
                leadingIcon = { Icon(Icons.Rounded.Person, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Bio
            SynopTrackTextField(
                value = uiState.bio,
                onValueChange = onBioChanged,
                label = "Bio",
                placeholder = "Tell us about yourself",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )

            // Email
            SynopTrackTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = "Email Address",
                placeholder = "Enter your email",
                leadingIcon = { Icon(Icons.Rounded.Email, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            // Phone (Optional / Data Collection)
            SynopTrackTextField(
                value = uiState.phoneNumber,
                onValueChange = onPhoneChanged,
                label = "Phone Number",
                placeholder = "934 567 8900",
                leadingIcon = { Icon(Icons.Rounded.Phone, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            // DOB
            Box(modifier = Modifier.fillMaxWidth()) {
                SynopTrackTextField(
                    value = uiState.dob,
                    onValueChange = {}, // Read only
                    label = "Date of Birth",
                    placeholder = "DD/MM/YYYY",
                    leadingIcon = { Icon(Icons.Rounded.CalendarToday, null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
                // Transparent clickable surface over the text field
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onDobClick() }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SynopTrackButton(
                text = "Complete Setup",
                onClick = onSubmit,
                enabled = uiState.isValid,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
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
