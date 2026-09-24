package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.EdvoraButton
import com.example.ui.components.EdvoraCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.EdvoraViewModel

@Composable
fun SubscriptionRenewalScreen(
    viewModel: EdvoraViewModel,
    onBackClick: () -> Unit
) {
    val school by viewModel.currentSchoolState.collectAsState()
    val schoolName = school?.name ?: "Delhi Public School, Vasant Kunj"
    val schoolId = school?.id ?: "SCH-1001"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(EdvoraCoral.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.LockReset, contentDescription = null, tint = EdvoraCoral, modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Subscription Renewal Required",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = "School Workspace: $schoolName",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(24.dp))

        EdvoraCard(
            backgroundColor = EdvoraCoral.copy(alpha = 0.1f),
            borderColor = EdvoraCoral
        ) {
            Text(
                text = "Workspace Features Restricted",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EdvoraCoral)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your free trial or subscription period has ended. Renew now to restore real-time AI fleet GPS telemetry, driver dispatch, parent notification engine, and student attendance logs.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Renewal Tier",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 1: Monthly Pro
        EdvoraCard(borderColor = EdvoraElectricBlue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Monthly Pro Plan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("₹4,999 / month", style = MaterialTheme.typography.titleMedium.copy(color = EdvoraElectricBlue, fontWeight = FontWeight.Bold))
                }
                EdvoraButton(
                    text = "Renew Pro",
                    onClick = { viewModel.renewSubscription(schoolId, "MONTHLY") },
                    modifier = Modifier.width(130.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 2: Yearly Enterprise
        EdvoraCard(borderColor = EdvoraEmerald) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Yearly Enterprise (20% Off)", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("₹49,999 / year", style = MaterialTheme.typography.titleMedium.copy(color = EdvoraEmerald, fontWeight = FontWeight.Bold))
                }
                EdvoraButton(
                    text = "Renew Yearly",
                    onClick = { viewModel.renewSubscription(schoolId, "YEARLY") },
                    containerColor = EdvoraEmerald,
                    modifier = Modifier.width(130.dp)
                )
            }
        }
    }
}
