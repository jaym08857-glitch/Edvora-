package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.EdvoraDataSeeder
import com.example.data.db.EdvoraDatabase
import com.example.data.model.*
import com.example.data.repository.EdvoraRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EdvoraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = EdvoraDatabase.getInstance(application)
    private val repository = EdvoraRepository(db)

    // Current Active Session
    val sessionState: StateFlow<SessionEntity?> = repository.sessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active School ID derived from Session or selected fallback
    private val _selectedSchoolId = MutableStateFlow<String?>("SCH-1001")
    val selectedSchoolId: StateFlow<String?> = _selectedSchoolId.asStateFlow()

    // All Schools (For Super Admin or Switcher)
    val allSchoolsState: StateFlow<List<SchoolEntity>> = repository.allSchoolsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Selected School Entity
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSchoolState: StateFlow<SchoolEntity?> = combine(
        sessionState,
        allSchoolsState,
        _selectedSchoolId
    ) { session, schools, selId ->
        val targetId = session?.schoolId?.ifEmpty { selId } ?: selId
        schools.find { it.id == targetId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Buses for current school
    @OptIn(ExperimentalCoroutinesApi::class)
    val busesState: StateFlow<List<BusEntity>> = currentSchoolState
        .flatMapLatest { school ->
            if (school != null) repository.getBusesForSchool(school.id)
            else repository.allBusesFlow
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Drivers for current school
    @OptIn(ExperimentalCoroutinesApi::class)
    val driversState: StateFlow<List<DriverEntity>> = currentSchoolState
        .flatMapLatest { school ->
            if (school != null) repository.getDriversForSchool(school.id)
            else repository.allDriversFlow
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Students for current school
    @OptIn(ExperimentalCoroutinesApi::class)
    val studentsState: StateFlow<List<StudentEntity>> = currentSchoolState
        .flatMapLatest { school ->
            if (school != null) repository.getStudentsForSchool(school.id)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Routes for current school
    @OptIn(ExperimentalCoroutinesApi::class)
    val routesState: StateFlow<List<RouteEntity>> = currentSchoolState
        .flatMapLatest { school ->
            if (school != null) repository.getRoutesForSchool(school.id)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Alerts for current school
    @OptIn(ExperimentalCoroutinesApi::class)
    val alertsState: StateFlow<List<AiAlertEntity>> = currentSchoolState
        .flatMapLatest { school ->
            if (school != null) repository.getAlertsForSchool(school.id)
            else repository.allAlertsFlow
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for current school
    @OptIn(ExperimentalCoroutinesApi::class)
    val notificationsState: StateFlow<List<NotificationEntity>> = currentSchoolState
        .flatMapLatest { school ->
            if (school != null) repository.getNotificationsForSchool(school.id)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Loading & Message state
    private val _uiState = MutableStateFlow(EdvoraUiState())
    val uiState: StateFlow<EdvoraUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed database with enterprise initial dataset
            EdvoraDataSeeder.seedInitialData(db)
        }
    }

    // AUTHENTICATION LOGIC

    fun loginSchoolAdmin(email: String, passwordHash: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = repository.getUserByEmail(email)
            if (user != null && user.passwordHash == passwordHash) {
                val session = SessionEntity(
                    userId = user.id,
                    schoolId = user.schoolId,
                    role = user.role,
                    userName = user.name,
                    userEmail = user.email,
                    token = "TOKEN_SA_${System.currentTimeMillis()}",
                    isLoggedIn = true
                )
                repository.saveSession(session)
                _selectedSchoolId.value = user.schoolId
                _uiState.update { it.copy(isLoading = false, successMessage = "Logged in successfully as School Admin") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid Email or Password") }
            }
        }
    }

    fun loginSchoolAdminOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (otp == "123456" || otp == "987654") {
                val user = repository.getUserByPhone(phone) ?: UserEntity(
                    id = "USR-SA-OTP-${System.currentTimeMillis()}",
                    schoolId = "SCH-1001",
                    role = "SCHOOL_ADMIN",
                    name = "School Admin",
                    email = "admin.otp@school.edu.in",
                    phone = phone,
                    passwordHash = "OTP",
                    isOtpAuth = true
                ).also { repository.insertUser(it) }

                val session = SessionEntity(
                    userId = user.id,
                    schoolId = user.schoolId.ifEmpty { "SCH-1001" },
                    role = user.role,
                    userName = user.name,
                    userEmail = user.email,
                    token = "TOKEN_SA_OTP_${System.currentTimeMillis()}",
                    isLoggedIn = true
                )
                repository.saveSession(session)
                _selectedSchoolId.value = session.schoolId
                _uiState.update { it.copy(isLoading = false, successMessage = "OTP verified successfully") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid OTP code. Use 123456 or 987654") }
            }
        }
    }

    fun loginDriver(phone: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (otp == "123456" || otp == "987654") {
                var user = repository.getUserByPhone(phone)
                if (user == null) {
                    user = UserEntity(
                        id = "USR-DRV-${System.currentTimeMillis()}",
                        schoolId = "SCH-1001",
                        role = "DRIVER",
                        name = "Assigned Driver",
                        email = "driver@dps.edu.in",
                        phone = phone,
                        passwordHash = "OTP",
                        isOtpAuth = true,
                        assignedBusId = "BUS-101"
                    )
                    repository.insertUser(user)
                }

                val session = SessionEntity(
                    userId = user.id,
                    schoolId = user.schoolId,
                    role = "DRIVER",
                    userName = user.name,
                    userEmail = user.email,
                    token = "TOKEN_DRV_${System.currentTimeMillis()}",
                    isLoggedIn = true
                )
                repository.saveSession(session)
                _selectedSchoolId.value = user.schoolId
                _uiState.update { it.copy(isLoading = false, successMessage = "Driver authenticated successfully") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid OTP code. Use 123456") }
            }
        }
    }

    fun loginSuperAdminStep1(email: String, passwordHash: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = repository.getUserByEmail(email)
            if (user != null && user.role == "SUPER_ADMIN" && user.passwordHash == passwordHash) {
                _uiState.update { it.copy(isLoading = false, requires2FA = true, pendingSuperAdminUser = user) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid Super Admin credentials") }
            }
        }
    }

    fun verify2FACode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = _uiState.value.pendingSuperAdminUser
            if (user != null && (code == "123456" || code == user.twoFactorSecret)) {
                val session = SessionEntity(
                    userId = user.id,
                    schoolId = "",
                    role = "SUPER_ADMIN",
                    userName = user.name,
                    userEmail = user.email,
                    token = "TOKEN_SUPER_${System.currentTimeMillis()}",
                    isLoggedIn = true
                )
                repository.saveSession(session)
                _uiState.update { it.copy(isLoading = false, requires2FA = false, pendingSuperAdminUser = null, successMessage = "Super Admin authenticated with 2FA") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid 2FA Authenticator Code. Use 123456") }
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(isLoading = false, successMessage = "Password reset link sent to $email") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
            _uiState.update { EdvoraUiState(successMessage = "Logged out securely") }
        }
    }

    // SCHOOL REGISTRATION & WORKSPACE ISOLATION

    fun registerNewSchool(
        schoolName: String,
        logoBadge: String,
        address: String,
        city: String,
        state: String,
        country: String,
        principalName: String,
        managerName: String,
        email: String,
        phone: String,
        totalStudents: Int,
        totalBuses: Int,
        plan: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val newSchoolId = "SCH-${1000 + (allSchoolsState.value.size + 1)}"
            val expiryTime = when (plan) {
                "FREE_TRIAL" -> System.currentTimeMillis() + (14L * 24 * 3600 * 1000)
                "MONTHLY" -> System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
                else -> System.currentTimeMillis() + (365L * 24 * 3600 * 1000)
            }

            val school = SchoolEntity(
                id = newSchoolId,
                name = schoolName,
                logoBadge = logoBadge.ifEmpty { schoolName.take(3).uppercase() },
                address = address,
                city = city,
                state = state,
                country = country,
                principalName = principalName,
                transportManagerName = managerName,
                email = email,
                phone = phone,
                totalStudents = totalStudents,
                totalBuses = totalBuses,
                subscriptionPlan = plan,
                subscriptionExpiryTimestamp = expiryTime
            )
            repository.insertSchool(school)

            // Auto-create School Admin user
            val adminUser = UserEntity(
                id = "USR-${newSchoolId}-ADM",
                schoolId = newSchoolId,
                role = "SCHOOL_ADMIN",
                name = managerName,
                email = email,
                phone = phone,
                passwordHash = "Password123!"
            )
            repository.insertUser(adminUser)

            // Auto-create initial default bus, route, and driver
            val busId = "BUS-${newSchoolId}-1"
            val bus = BusEntity(
                id = busId,
                schoolId = newSchoolId,
                busNumber = "BUS-01",
                plateNumber = "DL 02 SS 1001",
                capacity = 40,
                driverId = "DRV-${newSchoolId}-1",
                driverName = "Primary Driver",
                status = "IDLE",
                routeName = "Route 1 - Main Express",
                speedKmH = 0,
                lat = 28.6139,
                lng = 77.2090
            )
            repository.insertBus(bus)

            val driver = DriverEntity(
                id = "DRV-${newSchoolId}-1",
                schoolId = newSchoolId,
                name = "Primary Driver",
                phone = phone,
                licenseNumber = "DL-REGISTRATION-KEY",
                assignedBusId = busId,
                status = "ON_DUTY"
            )
            repository.insertDriver(driver)

            val route = RouteEntity(
                id = "RT-${newSchoolId}-1",
                schoolId = newSchoolId,
                routeName = "Route 1 - Main Express",
                startLocation = "$city Central Gate",
                endLocation = "$schoolName Campus",
                totalStops = 5,
                estimatedTimeMinutes = 30,
                status = "ACTIVE"
            )
            repository.insertRoute(route)

            // Auto-login into newly created isolated workspace
            val session = SessionEntity(
                userId = adminUser.id,
                schoolId = newSchoolId,
                role = "SCHOOL_ADMIN",
                userName = managerName,
                userEmail = email,
                token = "TOKEN_REG_${System.currentTimeMillis()}",
                isLoggedIn = true
            )
            repository.saveSession(session)
            _selectedSchoolId.value = newSchoolId

            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = "School '$schoolName' registered! Isolated workspace created."
                )
            }
        }
    }

    fun switchSchool(schoolId: String) {
        _selectedSchoolId.value = schoolId
        viewModelScope.launch {
            val session = sessionState.value
            if (session != null) {
                repository.saveSession(session.copy(schoolId = schoolId))
            }
        }
    }

    // ENTITY MANAGEMENT ACTIONS (Buses, Drivers, Students, Routes, Alerts)

    fun addBus(busNumber: String, plateNumber: String, capacity: Int, routeName: String) {
        val school = currentSchoolState.value ?: return
        viewModelScope.launch {
            val newBus = BusEntity(
                id = "BUS-${school.id}-${System.currentTimeMillis() % 1000}",
                schoolId = school.id,
                busNumber = busNumber,
                plateNumber = plateNumber,
                capacity = capacity,
                driverId = null,
                driverName = "Unassigned",
                status = "IDLE",
                routeName = routeName,
                speedKmH = 0,
                lat = 28.6139 + (Math.random() * 0.05),
                lng = 77.2090 + (Math.random() * 0.05)
            )
            repository.insertBus(newBus)
            _uiState.update { it.copy(successMessage = "Bus $busNumber added successfully") }
        }
    }

    fun addDriver(name: String, phone: String, licenseNumber: String, busId: String?) {
        val school = currentSchoolState.value ?: return
        viewModelScope.launch {
            val driverId = "DRV-${school.id}-${System.currentTimeMillis() % 1000}"
            val driver = DriverEntity(
                id = driverId,
                schoolId = school.id,
                name = name,
                phone = phone,
                licenseNumber = licenseNumber,
                assignedBusId = busId,
                status = "ON_DUTY"
            )
            repository.insertDriver(driver)

            if (busId != null) {
                val bus = repository.getBusById(busId)
                if (bus != null) {
                    repository.updateBus(bus.copy(driverId = driverId, driverName = name))
                }
            }
            _uiState.update { it.copy(successMessage = "Driver $name registered") }
        }
    }

    fun addStudent(name: String, grade: String, stopName: String, busId: String?, parentPhone: String) {
        val school = currentSchoolState.value ?: return
        viewModelScope.launch {
            val student = StudentEntity(
                id = "STD-${school.id}-${System.currentTimeMillis() % 1000}",
                schoolId = school.id,
                name = name,
                grade = grade,
                stopName = stopName,
                busId = busId,
                parentPhone = parentPhone,
                pickupStatus = "PENDING"
            )
            repository.insertStudent(student)
            _uiState.update { it.copy(successMessage = "Student $name enrolled") }
        }
    }

    fun markStudentPickupStatus(studentId: String, newStatus: String) {
        viewModelScope.launch {
            val student = repository.getStudentsForSchool(selectedSchoolId.value ?: "SCH-1001").firstOrNull()?.find { it.id == studentId }
            if (student != null) {
                val updatedTime = if (newStatus == "PICKED_UP") "07:45 AM" else null
                repository.updateStudent(student.copy(pickupStatus = newStatus, pickupTime = updatedTime))
            }
        }
    }

    fun resolveAlert(alertId: String) {
        viewModelScope.launch {
            repository.resolveAlert(alertId)
            _uiState.update { it.copy(successMessage = "AI Alert resolved") }
        }
    }

    fun updateBus(bus: BusEntity) {
        viewModelScope.launch {
            repository.updateBus(bus)
            _uiState.update { it.copy(successMessage = "Bus ${bus.busNumber} updated") }
        }
    }

    fun deleteBus(busId: String) {
        viewModelScope.launch {
            repository.deleteBus(busId)
            _uiState.update { it.copy(successMessage = "Bus deleted") }
        }
    }

    fun updateDriver(driver: DriverEntity) {
        viewModelScope.launch {
            repository.updateDriver(driver)
            _uiState.update { it.copy(successMessage = "Driver ${driver.name} updated") }
        }
    }

    fun deleteDriver(driverId: String) {
        viewModelScope.launch {
            repository.deleteDriver(driverId)
            _uiState.update { it.copy(successMessage = "Driver deleted") }
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
            _uiState.update { it.copy(successMessage = "Student ${student.name} updated") }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
            _uiState.update { it.copy(successMessage = "Student deleted") }
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun clearAllNotifications() {
        val schoolId = currentSchoolState.value?.id ?: "SCH-1001"
        viewModelScope.launch {
            repository.clearNotifications(schoolId)
            _uiState.update { it.copy(successMessage = "Notifications cleared") }
        }
    }

    fun renewSubscription(schoolId: String, plan: String) {
        viewModelScope.launch {
            val school = repository.getSchoolById(schoolId)
            if (school != null) {
                val addedDuration = if (plan == "YEARLY") 365L * 24 * 3600 * 1000 else 30L * 24 * 3600 * 1000
                val updated = school.copy(
                    subscriptionPlan = plan,
                    subscriptionExpiryTimestamp = System.currentTimeMillis() + addedDuration
                )
                repository.updateSchool(updated)
                _uiState.update { it.copy(successMessage = "Subscription renewed! Workspace unlocked.") }
            }
        }
    }

    fun clearUiMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

data class EdvoraUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val requires2FA: Boolean = false,
    val pendingSuperAdminUser: UserEntity? = null
)
