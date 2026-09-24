package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY createdAt DESC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    suspend fun getSchoolById(id: String): SchoolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: SchoolEntity)

    @Update
    suspend fun updateSchool(school: SchoolEntity)

    @Query("DELETE FROM schools WHERE id = :id")
    suspend fun deleteSchool(id: String)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE schoolId = :schoolId")
    fun getUsersBySchool(schoolId: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface BusDao {
    @Query("SELECT * FROM buses WHERE schoolId = :schoolId")
    fun getBusesForSchool(schoolId: String): Flow<List<BusEntity>>

    @Query("SELECT * FROM buses")
    fun getAllBuses(): Flow<List<BusEntity>>

    @Query("SELECT * FROM buses WHERE id = :id LIMIT 1")
    suspend fun getBusById(id: String): BusEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBus(bus: BusEntity)

    @Update
    suspend fun updateBus(bus: BusEntity)

    @Query("DELETE FROM buses WHERE id = :id")
    suspend fun deleteBus(id: String)
}

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers WHERE schoolId = :schoolId")
    fun getDriversForSchool(schoolId: String): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE id = :id LIMIT 1")
    suspend fun getDriverById(id: String): DriverEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    @Query("DELETE FROM drivers WHERE id = :id")
    suspend fun deleteDriver(id: String)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE schoolId = :schoolId")
    fun getStudentsForSchool(schoolId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE busId = :busId")
    fun getStudentsForBus(busId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudent(id: String)
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes WHERE schoolId = :schoolId")
    fun getRoutesForSchool(schoolId: String): Flow<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Query("DELETE FROM routes WHERE id = :id")
    suspend fun deleteRoute(id: String)
}

@Dao
interface AiAlertDao {
    @Query("SELECT * FROM ai_alerts WHERE schoolId = :schoolId ORDER BY timestamp DESC")
    fun getAlertsForSchool(schoolId: String): Flow<List<AiAlertEntity>>

    @Query("SELECT * FROM ai_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AiAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AiAlertEntity)

    @Query("UPDATE ai_alerts SET isResolved = 1 WHERE id = :id")
    suspend fun resolveAlert(id: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE schoolId = :schoolId ORDER BY timestamp DESC")
    fun getNotificationsForSchool(schoolId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("DELETE FROM notifications WHERE schoolId = :schoolId")
    suspend fun clearAllNotifications(schoolId: String)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE id = 1 LIMIT 1")
    fun getSessionFlow(): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE id = 1 LIMIT 1")
    suspend fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSession(session: SessionEntity)

    @Query("DELETE FROM sessions")
    suspend fun clearSession()
}
