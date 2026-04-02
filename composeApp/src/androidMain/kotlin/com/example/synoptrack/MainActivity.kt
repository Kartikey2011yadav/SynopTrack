package com.example.synoptrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.datastore.AppTheme
import com.example.synoptrack.core.navigation.AppNavHost
import com.example.synoptrack.core.presentation.components.SynopTrackToast
import com.example.synoptrack.core.presentation.model.ToastMessage
import com.example.synoptrack.core.presentation.util.ToastService
import com.example.synoptrack.core.theme.SynopTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var toastService: ToastService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val theme by viewModel.theme.collectAsState(initial = AppTheme.SYSTEM)

            SynopTrackTheme(appTheme = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavHost()
                        
                        // Global Toast Overlay
                        GlobalToastHost(toastService = toastService)
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalToastHost(toastService: ToastService) {
    var currentToast by remember { mutableStateOf<ToastMessage?>(null) }
    
    LaunchedEffect(toastService) {
        toastService.toastEvents.collect { message ->
            currentToast = message
            delay(message.duration)
            if (currentToast == message) {
                currentToast = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp), // Lift up slightly to avoid navbar overlap if any
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = currentToast != null,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            currentToast?.let { toast ->
                SynopTrackToast(
                    toast = toast,
                    onDismiss = { currentToast = null }
                )
            }
        }
    }
}
