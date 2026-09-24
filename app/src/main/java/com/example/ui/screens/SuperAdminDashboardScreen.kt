package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.SchoolEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EdvoraViewModel

@Composable
fun SuperAdminDashboardScreen(
    viewModel: EdvoraViewModel,
    onLogoutClick: () -> Unit,
    onRegisterNewSchoolClick: () -> Unit
) {
    val schools by viewModel.allSchoolsState.collectAsState()
    val allBuses by viewModel.busesState.collectAsState()

    var selectedSchoolToUpgrade by remember { mutableStateOf<SchoolEntity?>(null) }

    val totalStudentsCount = schools.sumOf { it.totalStudents }
    val totalBusesCount = schools.sumOf { it.totalBuses }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EdvoraGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Edvora Global HQ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Super Admin Platform Console",
                        style = MaterialTheme.typography.bodyMedium.copy(color = EdvoraGold)
                    )
                }
            }

            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.testTag("super_logout_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Logout", tint = EdvoraCoral)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Global Platform Metrics
        Row(modifier = Modifier.fillMaxWidth()) {
            StatMetricCard(
                title = "Total Registered Schools",
                value = "${schools.size}",
                icon = Icons.Default.School,
                accentColor = EdvoraElectricBlue,
                subtitle = "Isolated Workspaces",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            StatMetricCard(
                title = "Platform Fleet Scale",
                value = "$totalBusesCount",
                icon = Icons.Default.DirectionsBus,
                accentColor = EdvoraEmerald,
                subtitle = "$totalStudentsCount Students",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Platform System Health Banner
        EdvoraCard(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            borderColor = EdvoraEmerald
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = EdvoraEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Global SaaS Platform Health: OPTIMAL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Latency: 28ms • DB Load: 11% • AI Engine: ACTIVE",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
                StatusBadge(statusText = "99.99% Uptime", badgeColor = EdvoraEmerald)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Registered Schools List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tenant Workspaces (${schools.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Button(
                onClick = onRegisterNewSchoolClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Provision School")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(schools) { school ->
                val isActive = school.subscriptionExpiryTimestamp > System.currentTimeMillis()

                EdvoraCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EdvoraElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = school.logoBadge,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = school.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${school.city}, ${school.state} • ${school.email}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        StatusBadge(
                            statusText = if (isActive) school.subscriptionPlan else "EXPIRED",
                            badgeColor = if (isActive) EdvoraEmerald else EdvoraCoral
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Students: ${school.totalStudents} • Buses: ${school.totalBuses}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = EdvoraElectricBlue)
                        )

                        Row {
                            TextButton(onClick = {
                                viewModel.switchSchool(school.id)
                            }) {
                                Text("Inspect Workspace", color = EdvoraElectricBlue)
                            }

                            TextButton(onClick = {
                                selectedSchoolToUpgrade = school
                            }) {
                                Text("Renew Plan", color = EdvoraGold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedSchoolToUpgrade != null) {
        val schoolToRenew = selectedSchoolToUpgrade!!
        AlertDialog(
            onDismissRequest = { selectedSchoolToUpgrade = null },
            title = { Text("Renew Subscription for ${schoolToRenew.name}") },
            text = {
                Column {
                    Text("Select extension tier to unlock isolated school workspace:")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.renewSubscription(schoolToRenew.id, "MONTHLY")
                            selectedSchoolToUpgrade = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Renew Monthly Pro (30 Days)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.renewSubscription(schoolToRenew.id, "YEARLY")
                            selectedSchoolToUpgrade = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Renew Yearly Enterprise (1 Year)")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedSchoolToUpgrade = null }) { Text("Cancel") }
            }
        )
    }
}
