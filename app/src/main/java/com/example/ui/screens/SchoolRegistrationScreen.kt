package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.EdvoraButton
import com.example.ui.components.EdvoraCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.EdvoraViewModel

@Composable
fun SchoolRegistrationScreen(
    viewModel: EdvoraViewModel,
    onBackClick: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // Form fields
    var schoolName by remember { mutableStateOf("") }
    var logoBadge by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("India") }

    var principalName by remember { mutableStateOf("") }
    var managerName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var totalStudentsText by remember { mutableStateOf("600") }
    var totalBusesText by remember { mutableStateOf("12") }
    var selectedPlan by remember { mutableStateOf("FREE_TRIAL") }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "School Registration",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Provision an isolated enterprise workspace",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Indicator Row (Step 1, Step 2, Step 3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepIndicator(stepNumber = 1, title = "School Info", isActive = step >= 1, isCurrent = step == 1)
            Divider(modifier = Modifier.width(24.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            StepIndicator(stepNumber = 2, title = "Contacts", isActive = step >= 2, isCurrent = step == 2)
            Divider(modifier = Modifier.width(24.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            StepIndicator(stepNumber = 3, title = "Fleet & Plan", isActive = step >= 3, isCurrent = step == 3)
        }

        Spacer(modifier = Modifier.height(28.dp))

        when (step) {
            1 -> {
                Text(
                    text = "Step 1: School Profile & Address",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = {
                        schoolName = it
                        if (logoBadge.isEmpty() && it.length >= 3) {
                            logoBadge = it.take(3).uppercase()
                        }
                    },
                    label = { Text("School Full Name") },
                    placeholder = { Text("e.g. Modern High School") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_school_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = logoBadge,
                    onValueChange = { logoBadge = it.take(4).uppercase() },
                    label = { Text("School Logo Badge Code (3-4 Letters)") },
                    placeholder = { Text("MHS") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_logo_badge"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Campus Address") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_address"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f).testTag("reg_city"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f).testTag("reg_state"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                EdvoraButton(
                    text = "Continue to Step 2",
                    onClick = { if (schoolName.isNotBlank()) step = 2 },
                    testTag = "reg_step1_next"
                )
            }

            2 -> {
                Text(
                    text = "Step 2: Key Personnel Contacts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = principalName,
                    onValueChange = { principalName = it },
                    label = { Text("Principal Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_principal_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = managerName,
                    onValueChange = { managerName = it },
                    label = { Text("Transport Manager Name") },
                    leadingIcon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_manager_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Official Contact Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_email"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Official Mobile Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_phone"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { step = 1 },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    EdvoraButton(
                        text = "Continue to Step 3",
                        onClick = { if (managerName.isNotBlank() && email.isNotBlank()) step = 3 },
                        modifier = Modifier.weight(2f),
                        testTag = "reg_step2_next"
                    )
                }
            }

            3 -> {
                Text(
                    text = "Step 3: Fleet Scale & Subscription Plan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = totalStudentsText,
                        onValueChange = { totalStudentsText = it },
                        label = { Text("Total Students") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("reg_total_students"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = totalBusesText,
                        onValueChange = { totalBusesText = it },
                        label = { Text("Total Buses") },
                        leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("reg_total_buses"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Select Subscription Tier",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                PlanCard(
                    title = "14-Day Free Trial",
                    price = "Free (No CC Required)",
                    desc = "Full access to live telemetry, driver dispatch & student roster",
                    planKey = "FREE_TRIAL",
                    isSelected = selectedPlan == "FREE_TRIAL",
                    onSelect = { selectedPlan = "FREE_TRIAL" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PlanCard(
                    title = "Monthly Pro Plan",
                    price = "₹4,999 / month",
                    desc = "AI route optimization, speed warnings & parent SMS alerts",
                    planKey = "MONTHLY",
                    isSelected = selectedPlan == "MONTHLY",
                    onSelect = { selectedPlan = "MONTHLY" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PlanCard(
                    title = "Yearly Enterprise",
                    price = "₹49,999 / year (Save 20%)",
                    desc = "Dedicated support, custom reports & unlimited buses",
                    planKey = "YEARLY",
                    isSelected = selectedPlan == "YEARLY",
                    onSelect = { selectedPlan = "YEARLY" }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { step = 2 },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    EdvoraButton(
                        text = "Provision Isolated Workspace",
                        onClick = {
                            val students = totalStudentsText.toIntOrNull() ?: 500
                            val buses = totalBusesText.toIntOrNull() ?: 10
                            viewModel.registerNewSchool(
                                schoolName = schoolName,
                                logoBadge = logoBadge,
                                address = address,
                                city = city.ifEmpty { "Delhi" },
                                state = state.ifEmpty { "Delhi" },
                                country = country,
                                principalName = principalName.ifEmpty { "Principal" },
                                managerName = managerName,
                                email = email,
                                phone = phone.ifEmpty { "+91 98000 00000" },
                                totalStudents = students,
                                totalBuses = buses,
                                plan = selectedPlan
                            )
                        },
                        isLoading = uiState.isLoading,
                        modifier = Modifier.weight(2f),
                        testTag = "reg_finish_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCurrent: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) EdvoraElectricBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$stepNumber",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    desc: String,
    planKey: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    EdvoraCard(
        onClick = onSelect,
        borderColor = if (isSelected) EdvoraElectricBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        backgroundColor = if (isSelected) EdvoraElectricBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = price,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EdvoraElectricBlue
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
        }
    }
}
