package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ui.SchoolViewModel
import com.example.ui.UserSession
import com.example.ui.screens.AdminDashboard
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ParentDashboard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    // Retrieve AndroidViewModel using standard factory provider
    private val viewModel: SchoolViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SchoolViewModel(application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: SchoolViewModel) {
    val dbLoaded by viewModel.dbLoaded.collectAsState()
    val session by viewModel.session.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!dbLoaded) {
            // Spinner during database seed loading
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading IPAC International Database...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Animated session crossfade
            AnimatedContent(
                targetState = session,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "session_transition"
            ) { currentSession ->
                when (currentSession) {
                    is UserSession.Idle -> {
                        LoginScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    }
                    is UserSession.Parent -> {
                        ParentDashboard(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    }
                    is UserSession.Admin -> {
                        AdminDashboard(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
