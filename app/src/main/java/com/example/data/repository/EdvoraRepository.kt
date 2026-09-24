package com.example.data.repository

import com.example.data.db.EdvoraDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class EdvoraRepository(private val db: EdvoraDatabase) {

    // Session
    val sessionFlow: Flow<SessionEntity?> = db.sessionDao().getSessionFlow()
    suspend fun getSession(): SessionEntity? = db.sessionDao().getSession()
    suspend fun saveSession(session: SessionEntity) = db.sessionDao().setSession(session)
    suspend fun clearSession() = db.sessionDao().clearSession()

    // Auth
    suspend fun getUserByEmail(email: String): UserEntity? = db.userDao().getUserByEmail(email)
    suspend fun getUserByPhone(phone: String): UserEntity? = db.userDao().getUserByPhone(phone)
    suspend fun getUserById(id: String): UserEntity? = db.userDao().getUserById(id)
    suspend fun insertUser(user: UserEntity) = db.userDao().insertUser(user)

    // Schools
    val allSchoolsFlow: Flow<List<SchoolEntity>> = db.schoolDao().getAllSchools()
    suspend fun getSchoolById(id: String): SchoolEntity? = db.schoolDao().getSchoolById(id)
    suspend fun insertSchool(school: SchoolEntity) = db.schoolDao().insertSchool(school)
    suspend fun updateSchool(school: SchoolEntity) = db.schoolDao().updateSchool(school)
    suspend fun deleteSchool(id: String) = db.schoolDao().deleteSchool(id)

    // Buses
    fun getBusesForSchool(schoolId: String): Flow<List<BusEntity>> = db.busDao().getBusesForSchool(schoolId)
    val allBusesFlow: Flow<List<BusEntity>> = db.busDao().getAllBuses()
    suspend fun getBusById(id: String): BusEntity? = db.busDao().getBusById(id)
    suspend fun insertBus(bus: BusEntity) = db.busDao().insertBus(bus)
    suspend fun updateBus(bus: BusEntity) = db.busDao().updateBus(bus)
    suspend fun deleteBus(id: String) = db.busDao().deleteBus(id)

    // Drivers
    fun getDriversForSchool(schoolId: String): Flow<List<DriverEntity>> = db.driverDao().getDriversForSchool(schoolId)
    val allDriversFlow: Flow<List<DriverEntity>> = db.driverDao().getAllDrivers()
    suspend fun insertDriver(driver: DriverEntity) = db.driverDao().insertDriver(driver)
    suspend fun updateDriver(driver: DriverEntity) = db.driverDao().updateDriver(driver)
    suspend fun deleteDriver(id: String) = db.driverDao().deleteDriver(id)

    // Students
    fun getStudentsForSchool(schoolId: String): Flow<List<StudentEntity>> = db.studentDao().getStudentsForSchool(schoolId)
    fun getStudentsForBus(busId: String): Flow<List<StudentEntity>> = db.studentDao().getStudentsForBus(busId)
    suspend fun insertStudent(student: StudentEntity) = db.studentDao().insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = db.studentDao().updateStudent(student)
    suspend fun deleteStudent(id: String) = db.studentDao().deleteStudent(id)

    // Routes
    fun getRoutesForSchool(schoolId: String): Flow<List<RouteEntity>> = db.routeDao().getRoutesForSchool(schoolId)
    suspend fun insertRoute(route: RouteEntity) = db.routeDao().insertRoute(route)
    suspend fun updateRoute(route: RouteEntity) = db.routeDao().updateRoute(route)
    suspend fun deleteRoute(id: String) = db.routeDao().deleteRoute(id)

    // AI Alerts
    fun getAlertsForSchool(schoolId: String): Flow<List<AiAlertEntity>> = db.aiAlertDao().getAlertsForSchool(schoolId)
    val allAlertsFlow: Flow<List<AiAlertEntity>> = db.aiAlertDao().getAllAlerts()
    suspend fun insertAlert(alert: AiAlertEntity) = db.aiAlertDao().insertAlert(alert)
    suspend fun resolveAlert(id: String) = db.aiAlertDao().resolveAlert(id)

    // Notifications
    fun getNotificationsForSchool(schoolId: String): Flow<List<NotificationEntity>> = db.notificationDao().getNotificationsForSchool(schoolId)
    suspend fun insertNotification(notification: NotificationEntity) = db.notificationDao().insertNotification(notification)
    suspend fun markNotificationAsRead(id: String) = db.notificationDao().markAsRead(id)
    suspend fun clearNotifications(schoolId: String) = db.notificationDao().clearAllNotifications(schoolId)
}
