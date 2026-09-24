package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.EdvoraTheme
import com.example.ui.viewmodel.EdvoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EdvoraTheme {
                EdvoraAppMain()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdvoraAppMain(
    viewModel: EdvoraViewModel = viewModel()
) {
    val session by viewModel.sessionState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var navigationRoute by remember { mutableStateOf("ROLE_SELECT") }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessages()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val currentSession = session
            if (currentSession != null && currentSession.isLoggedIn) {
                when (currentSession.role) {
                    "SUPER_ADMIN" -> {
                        SuperAdminDashboardScreen(
                            viewModel = viewModel,
                            onLogoutClick = { viewModel.logout() },
                            onRegisterNewSchoolClick = { navigationRoute = "REGISTER_SCHOOL" }
                        )
                    }

                    "DRIVER" -> {
                        DriverDashboardScreen(
                            viewModel = viewModel,
                            onLogoutClick = { viewModel.logout() }
                        )
                    }

                    else -> { // SCHOOL_ADMIN
                        if (navigationRoute == "RENEWAL") {
                            SubscriptionRenewalScreen(
                                viewModel = viewModel,
                                onBackClick = { navigationRoute = "MAIN_DASHBOARD" }
                            )
                        } else {
                            SchoolAdminDashboardScreen(
                                viewModel = viewModel,
                                onLogoutClick = { viewModel.logout() },
                                onSwitchSchoolClick = {
                                    viewModel.logout()
                                    navigationRoute = "ROLE_SELECT"
                                },
                                onViewReportsClick = {
                                    // Reports trigger notification
                                },
                                onOpenRenewalScreen = {
                                    navigationRoute = "RENEWAL"
                                }
                            )
                        }
                    }
                }
            } else {
                when (navigationRoute) {
                    "SCHOOL_ADMIN_LOGIN" -> {
                        SchoolAdminLoginScreen(
                            viewModel = viewModel,
                            onBackClick = { navigationRoute = "ROLE_SELECT" }
                        )
                    }

                    "DRIVER_LOGIN" -> {
                        DriverLoginScreen(
                            viewModel = viewModel,
                            onBackClick = { navigationRoute = "ROLE_SELECT" }
                        )
                    }

                    "SUPER_ADMIN_LOGIN" -> {
                        SuperAdminLoginScreen(
                            viewModel = viewModel,
                            onBackClick = { navigationRoute = "ROLE_SELECT" }
                        )
                    }

                    "REGISTER_SCHOOL" -> {
                        SchoolRegistrationScreen(
                            viewModel = viewModel,
                            onBackClick = { navigationRoute = "ROLE_SELECT" }
                        )
                    }

                    else -> { // ROLE_SELECT
                        RoleSelectionScreen(
                            onSelectRole = { role ->
                                navigationRoute = when (role) {
                                    "SCHOOL_ADMIN" -> "SCHOOL_ADMIN_LOGIN"
                                    "DRIVER" -> "DRIVER_LOGIN"
                                    else -> "SUPER_ADMIN_LOGIN"
                                }
                            },
                            onRegisterSchool = { navigationRoute = "REGISTER_SCHOOL" }
                        )
                    }
                }
            }
        }
    }
}
