package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.StudentEntity
import com.example.ui.components.EdvoraButton
import com.example.ui.components.EdvoraCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.EdvoraViewModel

@Composable
fun DriverDashboardScreen(
    viewModel: EdvoraViewModel,
    onLogoutClick: () -> Unit
) {
    val school by viewModel.currentSchoolState.collectAsState()
    val buses by viewModel.busesState.collectAsState()
    val students by viewModel.studentsState.collectAsState()

    var isTripActive by remember { mutableStateOf(true) }

    val assignedBus = buses.firstOrNull { it.busNumber == "BUS-101" } ?: buses.firstOrNull()
    val schoolName = school?.name ?: "Delhi Public School, Vasant Kunj"

    val pickedUpCount = students.count { it.pickupStatus == "PICKED_UP" }
    val skippedCount = students.count { it.pickupStatus == "SKIPPED" }
    val pendingCount = students.count { it.pickupStatus == "PENDING" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Driver Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Driver Dispatch Console",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "Ramesh Kumar (Lic: DL-142011)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.testTag("driver_logout_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Logout", tint = EdvoraCoral)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bus & Trip Telemetry Banner Card
        EdvoraCard(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            borderColor = if (isTripActive) EdvoraEmerald else EdvoraGold
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = EdvoraElectricBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = assignedBus?.busNumber ?: "BUS-101",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "School: $schoolName",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "Plate: ${assignedBus?.plateNumber ?: "DL 01 AB 1234"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = EdvoraElectricBlue)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(
                        statusText = if (isTripActive) "TRIP IN PROGRESS" else "PAUSED",
                        badgeColor = if (isTripActive) EdvoraEmerald else EdvoraGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Speed: 42 km/h",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            EdvoraButton(
                text = if (isTripActive) "Pause Route Trip" else "Resume Route Trip",
                onClick = { isTripActive = !isTripActive },
                containerColor = if (isTripActive) EdvoraGold else EdvoraEmerald,
                contentColor = Color.Black,
                icon = if (isTripActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                testTag = "btn_toggle_trip"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pick-up Roster Status Summary Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricPill("Picked Up", "$pickedUpCount", EdvoraEmerald, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            MetricPill("Skipped", "$skippedCount", EdvoraGold, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            MetricPill("Pending", "$pendingCount", EdvoraElectricBlue, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Assigned Student Pick-Up Roster",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(students) { student ->
                StudentPickUpCard(
                    student = student,
                    onMarkPickup = { viewModel.markStudentPickupStatus(student.id, "PICKED_UP") },
                    onMarkSkip = { viewModel.markStudentPickupStatus(student.id, "SKIPPED") }
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}

@Composable
private fun StudentPickUpCard(
    student: StudentEntity,
    onMarkPickup: () -> Unit,
    onMarkSkip: () -> Unit
) {
    EdvoraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${student.grade} • ${student.stopName}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = "Parent Contact: ${student.parentPhone}",
                    style = MaterialTheme.typography.bodySmall.copy(color = EdvoraElectricBlue)
                )
            }

            StatusBadge(
                statusText = student.pickupStatus,
                badgeColor = when (student.pickupStatus) {
                    "PICKED_UP" -> EdvoraEmerald
                    "SKIPPED" -> EdvoraGold
                    else -> EdvoraElectricBlue
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = onMarkSkip,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EdvoraGold)
            ) {
                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Skip")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onMarkPickup,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EdvoraEmerald)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mark Picked Up")
            }
        }
    }
}
