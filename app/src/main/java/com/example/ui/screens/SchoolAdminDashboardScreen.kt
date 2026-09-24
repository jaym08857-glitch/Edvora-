package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EdvoraViewModel

@Composable
fun SchoolAdminDashboardScreen(
    viewModel: EdvoraViewModel,
    onLogoutClick: () -> Unit,
    onSwitchSchoolClick: () -> Unit,
    onViewReportsClick: () -> Unit,
    onOpenRenewalScreen: () -> Unit
) {
    val school by viewModel.currentSchoolState.collectAsState()
    val buses by viewModel.busesState.collectAsState()
    val drivers by viewModel.driversState.collectAsState()
    val students by viewModel.studentsState.collectAsState()
    val routes by viewModel.routesState.collectAsState()
    val alerts by viewModel.alertsState.collectAsState()
    val notifications by viewModel.notificationsState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Buses, 2: Drivers, 3: Students, 4: AI Insights
    var studentStatusFilter by remember { mutableStateOf<String?>(null) } // "PENDING", "PICKED_UP", "SKIPPED" or null

    // Search query
    var searchQuery by remember { mutableStateOf("") }

    // Dialog & Modal States
    var showAddBusDialog by remember { mutableStateOf(false) }
    var editingBus by remember { mutableStateOf<BusEntity?>(null) }
    var deletingBusId by remember { mutableStateOf<String?>(null) }

    var showAddDriverDialog by remember { mutableStateOf(false) }
    var editingDriver by remember { mutableStateOf<DriverEntity?>(null) }
    var deletingDriverId by remember { mutableStateOf<String?>(null) }

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var deletingStudentId by remember { mutableStateOf<String?>(null) }

    var showLiveMapModal by remember { mutableStateOf(false) }
    var selectedBusForMapDetail by remember { mutableStateOf<BusEntity?>(null) }

    var showNotificationsDrawer by remember { mutableStateOf(false) }
    var showReportsModal by remember { mutableStateOf(false) }

    val currentSchool = school ?: SchoolEntity(
        id = "SCH-1001",
        name = "Delhi Public School, Vasant Kunj",
        logoBadge = "DPS",
        address = "Sector C, Vasant Kunj",
        city = "New Delhi",
        state = "Delhi",
        country = "India",
        principalName = "Dr. S. Sharma",
        transportManagerName = "Rajesh Verma",
        email = "admin@dps.edu.in",
        phone = "+91 98100 12345",
        totalStudents = 1200,
        totalBuses = 24,
        subscriptionPlan = "YEARLY",
        subscriptionExpiryTimestamp = System.currentTimeMillis() + (365L * 24 * 3600 * 1000)
    )

    val isSubscriptionActive = currentSchool.subscriptionExpiryTimestamp > System.currentTimeMillis()
    val unreadNotificationsCount = notifications.count { !it.isRead }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SchoolBadgeHeader(
                        schoolName = currentSchool.name,
                        logoBadge = currentSchool.logoBadge,
                        city = currentSchool.city,
                        subscriptionPlan = currentSchool.subscriptionPlan,
                        isSubscriptionActive = isSubscriptionActive,
                        onSwitchSchoolClick = onSwitchSchoolClick,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notifications Bell with Badge
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge { Text("$unreadNotificationsCount") }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(
                            onClick = { showNotificationsDrawer = true },
                            modifier = Modifier.testTag("admin_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = EdvoraElectricBlue
                            )
                        }
                    }

                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = EdvoraCoral
                        )
                    }
                }

                if (!isSubscriptionActive) {
                    Spacer(modifier = Modifier.height(8.dp))
                    EdvoraCard(
                        backgroundColor = EdvoraCoral.copy(alpha = 0.15f),
                        borderColor = EdvoraCoral,
                        onClick = onOpenRenewalScreen
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = EdvoraCoral)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Subscription Expired! Renew to unlock full AI fleet features.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = EdvoraCoral,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = "RENEW NOW",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = EdvoraCoral,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Unified Global Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search student, bus number, driver, or parent phone...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EdvoraElectricBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; studentStatusFilter = null },
                        text = { Text("Dashboard Home", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Buses (${buses.size})", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Drivers (${drivers.size})", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Students (${students.size})", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Groups, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = { Text("AI Insights", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = null) }
                    )
                }
            }
        }

        // Global Search Results Popup Overlay if search query is present
        if (searchQuery.isNotBlank()) {
            val matchingStudents = students.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.parentPhone.contains(searchQuery) ||
                        it.grade.contains(searchQuery, ignoreCase = true)
            }
            val matchingBuses = buses.filter {
                it.busNumber.contains(searchQuery, ignoreCase = true) ||
                        it.plateNumber.contains(searchQuery, ignoreCase = true) ||
                        it.routeName.contains(searchQuery, ignoreCase = true)
            }
            val matchingDrivers = drivers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.phone.contains(searchQuery) ||
                        it.licenseNumber.contains(searchQuery, ignoreCase = true)
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Search Results for '$searchQuery'",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EdvoraElectricBlue)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (matchingStudents.isEmpty() && matchingBuses.isEmpty() && matchingDrivers.isEmpty()) {
                        Text("No matching records found.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                            if (matchingBuses.isNotEmpty()) {
                                item {
                                    Text("Buses (${matchingBuses.size}):", fontWeight = FontWeight.Bold)
                                }
                                items(matchingBuses) { bus ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTab = 1; searchQuery = "" }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${bus.busNumber} (${bus.routeName})", fontWeight = FontWeight.SemiBold)
                                        Text(bus.status, color = EdvoraEmerald)
                                    }
                                }
                            }

                            if (matchingStudents.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Students (${matchingStudents.size}):", fontWeight = FontWeight.Bold)
                                }
                                items(matchingStudents) { std ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTab = 3; searchQuery = "" }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${std.name} (${std.grade})", fontWeight = FontWeight.SemiBold)
                                        Text("Parent: ${std.parentPhone}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            if (matchingDrivers.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Drivers (${matchingDrivers.size}):", fontWeight = FontWeight.Bold)
                                }
                                items(matchingDrivers) { drv ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTab = 2; searchQuery = "" }
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${drv.name} - ${drv.phone}", fontWeight = FontWeight.SemiBold)
                                        Text(drv.status, color = EdvoraGold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Tab Content Area
        Box(modifier = Modifier.weight(1f).padding(16.dp)) {
            when (selectedTab) {
                0 -> OverviewHomeTab(
                    buses = buses,
                    drivers = drivers,
                    students = students,
                    routes = routes,
                    alerts = alerts,
                    onOpenLiveMap = { showLiveMapModal = true },
                    onViewReports = { showReportsModal = true },
                    onSelectBusMap = { bus ->
                        selectedBusForMapDetail = bus
                        showLiveMapModal = true
                    },
                    onMetricClick = { targetTab, status ->
                        selectedTab = targetTab
                        studentStatusFilter = status
                    },
                    onAddBus = { showAddBusDialog = true },
                    onAddDriver = { showAddDriverDialog = true },
                    onAddStudent = { showAddStudentDialog = true },
                    onResolveAlert = { viewModel.resolveAlert(it) }
                )

                1 -> BusesTabContent(
                    buses = buses,
                    onAddBusClick = { showAddBusDialog = true },
                    onEditBusClick = { editingBus = it },
                    onDeleteBusClick = { deletingBusId = it },
                    onToggleActive = { bus -> viewModel.updateBus(bus.copy(isActive = !bus.isActive)) }
                )

                2 -> DriversTabContent(
                    drivers = drivers,
                    onAddDriverClick = { showAddDriverDialog = true },
                    onEditDriverClick = { editingDriver = it },
                    onDeleteDriverClick = { deletingDriverId = it }
                )

                3 -> StudentsTabContent(
                    students = students,
                    filterStatus = studentStatusFilter,
                    onClearFilter = { studentStatusFilter = null },
                    onAddStudentClick = { showAddStudentDialog = true },
                    onEditStudentClick = { editingStudent = it },
                    onDeleteStudentClick = { deletingStudentId = it },
                    onStatusChange = { id, status -> viewModel.markStudentPickupStatus(id, status) }
                )

                4 -> AiDashboardTab(
                    alerts = alerts,
                    buses = buses,
                    onResolveAlert = { viewModel.resolveAlert(it) }
                )
            }
        }
    }

    // Modal Dialogs & Sheets

    if (showAddBusDialog) {
        AddOrEditBusDialog(
            bus = null,
            drivers = drivers,
            onDismiss = { showAddBusDialog = false },
            onConfirm = { busNumber, plate, cap, route, driverId, gpsSource ->
                val drv = drivers.find { it.id == driverId }
                viewModel.addBus(busNumber, plate, cap, route)
                showAddBusDialog = false
            }
        )
    }

    editingBus?.let { busToEdit ->
        AddOrEditBusDialog(
            bus = busToEdit,
            drivers = drivers,
            onDismiss = { editingBus = null },
            onConfirm = { busNum, plate, cap, route, drvId, gpsSrc ->
                val drv = drivers.find { it.id == drvId }
                viewModel.updateBus(
                    busToEdit.copy(
                        busNumber = busNum,
                        plateNumber = plate,
                        capacity = cap,
                        routeName = route,
                        driverId = drvId,
                        driverName = drv?.name ?: busToEdit.driverName,
                        gpsSource = gpsSrc
                    )
                )
                editingBus = null
            }
        )
    }

    deletingBusId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingBusId = null },
            title = { Text("Delete Bus Record") },
            text = { Text("Are you sure you want to remove this bus from the fleet roster?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBus(id)
                        deletingBusId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EdvoraCoral)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deletingBusId = null }) { Text("Cancel") }
            }
        )
    }

    if (showAddDriverDialog) {
        AddOrEditDriverDialog(
            driver = null,
            buses = buses,
            onDismiss = { showAddDriverDialog = false },
            onConfirm = { name, phone, lic, busId, exp ->
                viewModel.addDriver(name, phone, lic, busId)
                showAddDriverDialog = false
            }
        )
    }

    editingDriver?.let { drvToEdit ->
        AddOrEditDriverDialog(
            driver = drvToEdit,
            buses = buses,
            onDismiss = { editingDriver = null },
            onConfirm = { name, phone, lic, busId, exp ->
                viewModel.updateDriver(
                    drvToEdit.copy(
                        name = name,
                        phone = phone,
                        licenseNumber = lic,
                        assignedBusId = busId,
                        experience = exp
                    )
                )
                editingDriver = null
            }
        )
    }

    deletingDriverId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingDriverId = null },
            title = { Text("Delete Driver") },
            text = { Text("Are you sure you want to remove this driver from the roster?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDriver(id)
                        deletingDriverId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EdvoraCoral)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deletingDriverId = null }) { Text("Cancel") }
            }
        )
    }

    if (showAddStudentDialog) {
        AddOrEditStudentDialog(
            student = null,
            buses = buses,
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { name, grade, sec, stop, busId, parentPhone, parentName, addr, notes ->
                viewModel.addStudent(name, grade, stop, busId, parentPhone)
                showAddStudentDialog = false
            }
        )
    }

    editingStudent?.let { stdToEdit ->
        AddOrEditStudentDialog(
            student = stdToEdit,
            buses = buses,
            onDismiss = { editingStudent = null },
            onConfirm = { name, grade, sec, stop, busId, parentPhone, parentName, addr, notes ->
                viewModel.updateStudent(
                    stdToEdit.copy(
                        name = name,
                        grade = grade,
                        section = sec,
                        stopName = stop,
                        busId = busId,
                        parentPhone = parentPhone,
                        parentName = parentName,
                        homeAddress = addr,
                        specialNotes = notes
                    )
                )
                editingStudent = null
            }
        )
    }

    deletingStudentId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingStudentId = null },
            title = { Text("Delete Student Record") },
            text = { Text("Are you sure you want to unenroll this student?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(id)
                        deletingStudentId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EdvoraCoral)
                ) { Text("Unenroll") }
            },
            dismissButton = {
                TextButton(onClick = { deletingStudentId = null }) { Text("Cancel") }
            }
        )
    }

    if (showLiveMapModal) {
        LiveFleetMapModal(
            buses = buses,
            students = students,
            selectedBus = selectedBusForMapDetail,
            schoolName = currentSchool.name,
            onDismiss = {
                showLiveMapModal = false
                selectedBusForMapDetail = null
            }
        )
    }

    if (showNotificationsDrawer) {
        NotificationsModal(
            notifications = notifications,
            onDismiss = { showNotificationsDrawer = false },
            onMarkRead = { id -> viewModel.markNotificationAsRead(id) },
            onClearAll = { viewModel.clearAllNotifications() }
        )
    }

    if (showReportsModal) {
        ReportsGeneratorModal(
            buses = buses,
            drivers = drivers,
            students = students,
            onDismiss = { showReportsModal = false }
        )
    }
}

@Composable
private fun OverviewHomeTab(
    buses: List<BusEntity>,
    drivers: List<DriverEntity>,
    students: List<StudentEntity>,
    routes: List<RouteEntity>,
    alerts: List<AiAlertEntity>,
    onOpenLiveMap: () -> Unit,
    onViewReports: () -> Unit,
    onSelectBusMap: (BusEntity) -> Unit,
    onMetricClick: (Int, String?) -> Unit,
    onAddBus: () -> Unit,
    onAddDriver: () -> Unit,
    onAddStudent: () -> Unit,
    onResolveAlert: (String) -> Unit
) {
    val activeBusesCount = buses.count { it.status == "ON_THE_WAY" || it.speedKmH > 0 }
    val activeDriversCount = drivers.count { it.status == "IN_TRANSIT" || it.status == "ON_DUTY" }
    val pickedUpCount = students.count { it.pickupStatus == "PICKED_UP" }
    val pendingCount = students.count { it.pickupStatus == "PENDING" }
    val skippedCount = students.count { it.pickupStatus == "SKIPPED" }
    val unresolvedAlerts = alerts.filter { !it.isResolved }
    val highAlertsCount = unresolvedAlerts.count { it.severity == "HIGH" }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Transport Operations Summary",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 9 Clickable Metric Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                StatMetricCard(
                    title = "Total Students",
                    value = "${students.size}",
                    icon = Icons.Default.Groups,
                    accentColor = EdvoraElectricBlue,
                    subtitle = "Click to View Directory",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_students",
                    onClick = { onMetricClick(3, null) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatMetricCard(
                    title = "Total Drivers",
                    value = "${drivers.size}",
                    icon = Icons.Default.Person,
                    accentColor = EdvoraEmerald,
                    subtitle = "$activeDriversCount On Duty",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_drivers",
                    onClick = { onMetricClick(2, null) }
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                StatMetricCard(
                    title = "Total Buses",
                    value = "${buses.size}",
                    icon = Icons.Default.DirectionsBus,
                    accentColor = EdvoraPurple,
                    subtitle = "$activeBusesCount Active Transmitting",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_buses",
                    onClick = { onMetricClick(1, null) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatMetricCard(
                    title = "Active Routes",
                    value = "${routes.size}",
                    icon = Icons.Default.AltRoute,
                    accentColor = EdvoraGold,
                    subtitle = "All Scheduled Routes",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_routes",
                    onClick = { onMetricClick(0, null) }
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                StatMetricCard(
                    title = "Today's Pickups",
                    value = "${students.size}",
                    icon = Icons.Default.CheckCircle,
                    accentColor = EdvoraElectricBlue,
                    subtitle = "$pickedUpCount Completed • $pendingCount Pending",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_pickups",
                    onClick = { onMetricClick(3, null) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatMetricCard(
                    title = "Completed Pickups",
                    value = "$pickedUpCount",
                    icon = Icons.Default.FactCheck,
                    accentColor = EdvoraEmerald,
                    subtitle = "Safely Onboard",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_completed",
                    onClick = { onMetricClick(3, "PICKED_UP") }
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                StatMetricCard(
                    title = "Pending Pickups",
                    value = "$pendingCount",
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = EdvoraGold,
                    subtitle = "En Route",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_pending",
                    onClick = { onMetricClick(3, "PENDING") }
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatMetricCard(
                    title = "AI Alerts",
                    value = "${unresolvedAlerts.size}",
                    icon = Icons.Default.SmartToy,
                    accentColor = if (unresolvedAlerts.isNotEmpty()) EdvoraCoral else EdvoraEmerald,
                    subtitle = if (unresolvedAlerts.isNotEmpty()) "$highAlertsCount High Severity" else "All Systems Nominal",
                    modifier = Modifier.weight(1f),
                    testTag = "metric_alerts",
                    onClick = { onMetricClick(4, null) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Executive Dispatch Actions
        Text(
            text = "Fleet Dispatch & Operations",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EdvoraButton(
                text = "Live Interactive Map",
                onClick = onOpenLiveMap,
                icon = Icons.Default.Map,
                modifier = Modifier.weight(1.3f),
                containerColor = EdvoraElectricBlue,
                testTag = "btn_live_map"
            )

            Spacer(modifier = Modifier.width(8.dp))

            EdvoraButton(
                text = "Reports & Analytics",
                onClick = onViewReports,
                icon = Icons.Default.BarChart,
                modifier = Modifier.weight(1f),
                containerColor = EdvoraDarkCard,
                contentColor = MaterialTheme.colorScheme.onSurface,
                testTag = "btn_reports"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onAddBus,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Bus")
            }
            Spacer(modifier = Modifier.width(6.dp))
            OutlinedButton(
                onClick = onAddDriver,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Driver")
            }
            Spacer(modifier = Modifier.width(6.dp))
            OutlinedButton(
                onClick = onAddStudent,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Student")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Active Buses Live Status Bar
        Text(
            text = "Active Vehicles Telemetry Quick View",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(buses) { bus ->
                val badgeColor = when (bus.delayStatusColor) {
                    "RED" -> EdvoraCoral
                    "YELLOW" -> EdvoraGold
                    else -> EdvoraEmerald
                }

                EdvoraCard(
                    modifier = Modifier.width(220.dp),
                    onClick = { onSelectBusMap(bus) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(bus.busNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Driver: ${bus.driverName ?: "Unassigned"}", style = MaterialTheme.typography.bodySmall)
                    Text("Speed: ${bus.speedKmH} km/h • ETA: ${bus.etaMinutes}m", style = MaterialTheme.typography.bodySmall, color = EdvoraElectricBlue)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { bus.routeProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = EdvoraElectricBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Anomaly & Safety Log Stream
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = EdvoraElectricBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Operational & Safety Anomaly Logs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Text(
                text = "${alerts.size} Logs",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (alerts.isEmpty()) {
            EdvoraCard {
                Text(
                    text = "No safety or route anomaly alerts reported today.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = EdvoraEmerald)
                )
            }
        } else {
            alerts.forEach { alert ->
                AiAlertCard(
                    title = alert.title,
                    message = alert.message,
                    severity = alert.severity,
                    timeAgo = "Live Telemetry",
                    isResolved = alert.isResolved,
                    onResolveClick = { onResolveAlert(alert.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BusesTabContent(
    buses: List<BusEntity>,
    onAddBusClick: () -> Unit,
    onEditBusClick: (BusEntity) -> Unit,
    onDeleteBusClick: (String) -> Unit,
    onToggleActive: (BusEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bus Fleet Management",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = onAddBusClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Bus")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(buses) { bus ->
                val statusColor = when (bus.delayStatusColor) {
                    "RED" -> EdvoraCoral
                    "YELLOW" -> EdvoraGold
                    else -> EdvoraEmerald
                }

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
                                    .background(EdvoraElectricBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = EdvoraElectricBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = bus.busNumber,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Plate: ${bus.plateNumber} • Capacity: ${bus.capacity} seats",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        StatusBadge(
                            statusText = bus.status,
                            badgeColor = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Driver: ${bus.driverName ?: "Unassigned"}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "GPS Source: ${bus.gpsSource}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (bus.isActive) "ACTIVE" else "INACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (bus.isActive) EdvoraEmerald else EdvoraCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = bus.isActive,
                                onCheckedChange = { onToggleActive(bus) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Route: ${bus.routeName}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Route Completion: ${(bus.routeProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "ETA: ${bus.etaMinutes} mins",
                            style = MaterialTheme.typography.bodySmall.copy(color = EdvoraElectricBlue, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { bus.routeProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = EdvoraElectricBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Edit / Delete Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { onEditBusClick(bus) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Bus", tint = EdvoraElectricBlue)
                        }
                        IconButton(onClick = { onDeleteBusClick(bus.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Bus", tint = EdvoraCoral)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriversTabContent(
    drivers: List<DriverEntity>,
    onAddDriverClick: () -> Unit,
    onEditDriverClick: (DriverEntity) -> Unit,
    onDeleteDriverClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Driver Management Roster",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = onAddDriverClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Driver")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(drivers) { driver ->
                EdvoraCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(EdvoraEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = EdvoraEmerald,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = driver.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Phone: ${driver.phone}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        StatusBadge(
                            statusText = driver.status,
                            badgeColor = if (driver.status == "IN_TRANSIT" || driver.status == "ON_DUTY") EdvoraEmerald else EdvoraGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "License #: ${driver.licenseNumber} • Experience: ${driver.experience} • Rating: ⭐ ${driver.rating}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assigned Bus ID: ${driver.assignedBusId ?: "Unassigned"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = EdvoraElectricBlue, fontWeight = FontWeight.Bold)
                        )

                        Row {
                            IconButton(onClick = { onEditDriverClick(driver) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Driver", tint = EdvoraElectricBlue)
                            }
                            IconButton(onClick = { onDeleteDriverClick(driver.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Driver", tint = EdvoraCoral)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentsTabContent(
    students: List<StudentEntity>,
    filterStatus: String?,
    onClearFilter: () -> Unit,
    onAddStudentClick: () -> Unit,
    onEditStudentClick: (StudentEntity) -> Unit,
    onDeleteStudentClick: (String) -> Unit,
    onStatusChange: (String, String) -> Unit
) {
    val displayStudents = if (filterStatus != null) {
        students.filter { it.pickupStatus == filterStatus }
    } else students

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Student Directory",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                if (filterStatus != null) {
                    Text(
                        text = "Filtered by status: $filterStatus",
                        style = MaterialTheme.typography.bodySmall.copy(color = EdvoraElectricBlue)
                    )
                }
            }

            Row {
                if (filterStatus != null) {
                    TextButton(onClick = onClearFilter) {
                        Text("Clear Filter")
                    }
                }
                Button(
                    onClick = onAddStudentClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Student")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (displayStudents.isEmpty()) {
            EdvoraCard {
                Text(
                    text = "No students match the current filter selection.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(displayStudents) { student ->
                    EdvoraCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(EdvoraElectricBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = null,
                                        tint = EdvoraElectricBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${student.name} (${student.grade}-${student.section})",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Stop: ${student.stopName} • Order #${student.pickupOrder}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
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

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Parent: ${student.parentName} (${student.parentPhone})",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "Address: ${student.homeAddress}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        if (student.specialNotes.isNotBlank()) {
                            Text(
                                text = "Notes: ${student.specialNotes}",
                                style = MaterialTheme.typography.bodySmall.copy(color = EdvoraCoral, fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                TextButton(onClick = { onStatusChange(student.id, "PICKED_UP") }) {
                                    Text("Pickup", color = EdvoraEmerald, fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = { onStatusChange(student.id, "SKIPPED") }) {
                                    Text("Skip", color = EdvoraGold, fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = { onStatusChange(student.id, "PENDING") }) {
                                    Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Row {
                                IconButton(onClick = { onEditStudentClick(student) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Student", tint = EdvoraElectricBlue)
                                }
                                IconButton(onClick = { onDeleteStudentClick(student.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Student", tint = EdvoraCoral)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiDashboardTab(
    alerts: List<AiAlertEntity>,
    buses: List<BusEntity>,
    onResolveAlert: (String) -> Unit
) {
    var appliedRerouteMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "AI Operational & Predictive Telemetry",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Autonomous fleet optimization, route rerouting, and safety monitoring.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // AI Summary Banner
        EdvoraCard(
            backgroundColor = EdvoraElectricBlue.copy(alpha = 0.1f),
            borderColor = EdvoraElectricBlue
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EdvoraElectricBlue, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Today's AI Fleet Health Rating: 94%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EdvoraElectricBlue))
                    Text("AI active engines: Geofence Monitor, Predictive Brake Wear, Speed Limit Safeguard.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (appliedRerouteMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            EdvoraCard(backgroundColor = EdvoraEmerald.copy(alpha = 0.15f), borderColor = EdvoraEmerald) {
                Text(appliedRerouteMessage!!, color = EdvoraEmerald, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Route Optimization Recommendation
        Text(
            text = "AI Route Optimization Suggestion",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        EdvoraCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Traffic Jam Detected on Vasant Marg (22 min delay)", fontWeight = FontWeight.Bold)
                    Text("AI suggests rerouting Bus #101 via Outer Ring Road to save 14 minutes and bypass traffic.", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        appliedRerouteMessage = "AI Reroute applied! Bus #101 driver notified via in-app GPS."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EdvoraElectricBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Apply AI Route")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Safety & Speed Anomaly Logs
        Text(
            text = "Active Anomaly Warnings (${alerts.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        alerts.forEach { alert ->
            AiAlertCard(
                title = alert.title,
                message = alert.message,
                severity = alert.severity,
                timeAgo = "Telemetry Sync",
                isResolved = alert.isResolved,
                onResolveClick = { onResolveAlert(alert.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Dialogs & Modals

@Composable
fun AddOrEditBusDialog(
    bus: BusEntity?,
    drivers: List<DriverEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, String?, String) -> Unit
) {
    var busNumber by remember { mutableStateOf(bus?.busNumber ?: "BUS-105") }
    var plateNumber by remember { mutableStateOf(bus?.plateNumber ?: "DL 01 GH 3456") }
    var capacityText by remember { mutableStateOf(bus?.capacity?.toString() ?: "45") }
    var routeName by remember { mutableStateOf(bus?.routeName ?: "Route 5 - Vasant Vihar Express") }
    var selectedDriverId by remember { mutableStateOf(bus?.driverId ?: drivers.firstOrNull()?.id) }
    var gpsSource by remember { mutableStateOf(bus?.gpsSource ?: "Driver Mobile GPS") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (bus == null) "Add Bus to Fleet" else "Edit Bus Details", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = busNumber,
                    onValueChange = { busNumber = it },
                    label = { Text("Bus Code Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = plateNumber,
                    onValueChange = { plateNumber = it },
                    label = { Text("License Registration Plate") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    label = { Text("Seating Capacity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = routeName,
                    onValueChange = { routeName = it },
                    label = { Text("Assigned Route") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = gpsSource,
                    onValueChange = { gpsSource = it },
                    label = { Text("GPS Telemetry Hardware Source") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(busNumber, plateNumber, capacityText.toIntOrNull() ?: 40, routeName, selectedDriverId, gpsSource)
            }) { Text(if (bus == null) "Add Bus" else "Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddOrEditDriverDialog(
    driver: DriverEntity?,
    buses: List<BusEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String?, String) -> Unit
) {
    var name by remember { mutableStateOf(driver?.name ?: "") }
    var phone by remember { mutableStateOf(driver?.phone ?: "+91 ") }
    var licenseNumber by remember { mutableStateOf(driver?.licenseNumber ?: "DL-") }
    var experience by remember { mutableStateOf(driver?.experience ?: "6 Years") }
    var selectedBusId by remember { mutableStateOf(driver?.assignedBusId ?: buses.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (driver == null) "Register Driver" else "Edit Driver Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Driver Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Driving License Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Experience Years") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) onConfirm(name, phone, licenseNumber, selectedBusId, experience)
            }) { Text(if (driver == null) "Register Driver" else "Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddOrEditStudentDialog(
    student: StudentEntity?,
    buses: List<BusEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String?, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var grade by remember { mutableStateOf(student?.grade ?: "Class 6") }
    var section by remember { mutableStateOf(student?.section ?: "A") }
    var stopName by remember { mutableStateOf(student?.stopName ?: "") }
    var parentName by remember { mutableStateOf(student?.parentName ?: "") }
    var parentPhone by remember { mutableStateOf(student?.parentPhone ?: "+91 ") }
    var homeAddress by remember { mutableStateOf(student?.homeAddress ?: "Vasant Kunj, New Delhi") }
    var specialNotes by remember { mutableStateOf(student?.specialNotes ?: "Front seat preference") }
    var selectedBusId by remember { mutableStateOf(student?.busId ?: buses.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Enroll Student" else "Edit Student Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = grade,
                        onValueChange = { grade = it },
                        label = { Text("Class/Grade") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Section") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = stopName,
                    onValueChange = { stopName = it },
                    label = { Text("Pickup Stop Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = parentPhone,
                    onValueChange = { parentPhone = it },
                    label = { Text("Parent Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = homeAddress,
                    onValueChange = { homeAddress = it },
                    label = { Text("Home Residential Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = specialNotes,
                    onValueChange = { specialNotes = it },
                    label = { Text("Special Medical or Seating Requests") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) onConfirm(name, grade, section, stopName, selectedBusId, parentPhone, parentName, homeAddress, specialNotes)
            }) { Text(if (student == null) "Enroll Student" else "Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun LiveFleetMapModal(
    buses: List<BusEntity>,
    students: List<StudentEntity>,
    selectedBus: BusEntity?,
    schoolName: String,
    onDismiss: () -> Unit
) {
    var activeFocusBus by remember { mutableStateOf(selectedBus ?: buses.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().height(620.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Live Satellite GPS Map", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text(schoolName, style = MaterialTheme.typography.bodySmall.copy(color = EdvoraElectricBlue))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                // Interactive Radar Map Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(EdvoraDeepNavy)
                        .border(1.dp, EdvoraElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Radar",
                            tint = EdvoraElectricBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "LIVE SATELLITE RADAR • ACTIVE FLEET",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        activeFocusBus?.let { focus ->
                            Text(
                                text = "Focused: ${focus.busNumber} (${focus.speedKmH} km/h • ETA ${focus.etaMinutes}m)",
                                style = MaterialTheme.typography.bodySmall.copy(color = EdvoraEmerald, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Select Bus to View Real-time Telemetry:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(buses) { bus ->
                        val isSel = bus.id == activeFocusBus?.id
                        val badgeColor = when (bus.delayStatusColor) {
                            "RED" -> EdvoraCoral
                            "YELLOW" -> EdvoraGold
                            else -> EdvoraEmerald
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EdvoraElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { activeFocusBus = bus }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(8.dp).clip(CircleShape).background(badgeColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = bus.busNumber,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                activeFocusBus?.let { bus ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Driver: ${bus.driverName ?: "Unassigned"}", fontWeight = FontWeight.Bold)
                                Text("Phone: ${bus.driverPhone ?: "+91 98765 43210"}", color = EdvoraElectricBlue)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Current Location Pin: Lat ${bus.lat}, Lng ${bus.lng}", style = MaterialTheme.typography.bodySmall)
                            Text("Students Picked Up: ${bus.studentsPickedUp} • Remaining: ${bus.studentsRemaining}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close Map") }
        }
    )
}

@Composable
fun NotificationsModal(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit,
    onClearAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().height(520.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Operational Notifications", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                TextButton(onClick = onClearAll) { Text("Clear All") }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Text("No active notifications at this time.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notifications) { notif ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (!notif.isRead) EdvoraElectricBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().clickable { onMarkRead(notif.id) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(notif.title, fontWeight = FontWeight.Bold)
                                    if (!notif.isRead) {
                                        Text("NEW", color = EdvoraElectricBlue, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(notif.message, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun ReportsGeneratorModal(
    buses: List<BusEntity>,
    drivers: List<DriverEntity>,
    students: List<StudentEntity>,
    onDismiss: () -> Unit
) {
    var exportSuccessMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().height(560.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fleet Performance Reports", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                if (exportSuccessMsg != null) {
                    EdvoraCard(backgroundColor = EdvoraEmerald.copy(alpha = 0.15f), borderColor = EdvoraEmerald) {
                        Text(exportSuccessMsg!!, color = EdvoraEmerald, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text("Performance Summary Metrics:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("• On-Time Pickup Compliance: 96.2%", fontWeight = FontWeight.Bold, color = EdvoraEmerald)
                        Text("• Fuel Efficiency Score: 8.4 km/L")
                        Text("• Driver Safety Rating Average: 4.8 / 5.0")
                        Text("• Total Students Transported Today: ${students.count { it.pickupStatus == "PICKED_UP" }}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Export Official Fleet Log Reports:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { exportSuccessMsg = "Daily PDF Fleet Report exported to downloads folder." },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EdvoraElectricBlue)
                    ) {
                        Text("Export PDF")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { exportSuccessMsg = "Monthly CSV Telemetry dataset downloaded." },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EdvoraDarkCard)
                    ) {
                        Text("Export CSV")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}
