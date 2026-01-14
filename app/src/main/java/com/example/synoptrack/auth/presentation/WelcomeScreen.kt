package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.synoptrack.R
import com.example.synoptrack.core.theme.ElectricBluePrimary

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onTermsClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    Surface(
        color = Color.Black, // Premium Dark
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo & Title
            androidx.compose.foundation.Image(
                 painter = painterResource(id = R.mipmap.ic_launcher_round),
                 contentDescription = "App Logo",
                 modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "SynopTrack",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = ElectricBluePrimary
            )
            Text(
                text = "Connect with your Friends.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Actions
            AuthButton(
                text = "Login",
                icon = null,
                onClick = onLogin,
                containerColor = ElectricBluePrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AuthButton(
                text = "Create New Account",
                icon = null,
                onClick = onCreateAccount,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Terms
            val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                append("By continuing, you agree to our ")
                
                pushStringAnnotation(tag = "terms", annotation = "https://synoptrack.com/terms")
                withStyle(style = SpanStyle(color = ElectricBluePrimary, textDecoration = TextDecoration.Underline)) {
                    append("Terms & Privacy Policy")
                }
                pop()
                append(".")
            }
            
            androidx.compose.foundation.text.ClickableText(
                text = annotatedString,
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                ),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset).firstOrNull()?.let {
                        onTermsClick()
                    }
                }
            )
        }
    }
}

@Composable
fun AuthButton(
    text: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    containerColor: Color = ElectricBluePrimary,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}
