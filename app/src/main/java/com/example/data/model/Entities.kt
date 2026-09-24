package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logoBadge: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val principalName: String,
    val transportManagerName: String,
    val email: String,
    val phone: String,
    val totalStudents: Int,
    val totalBuses: Int,
    val subscriptionPlan: String, // "FREE_TRIAL", "MONTHLY", "YEARLY"
    val subscriptionExpiryTimestamp: Long,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val schoolId: String, // Empty for Super Admin
    val role: String, // "SUPER_ADMIN", "SCHOOL_ADMIN", "DRIVER"
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val isOtpAuth: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val twoFactorSecret: String? = null,
    val assignedBusId: String? = null
)

@Entity(tableName = "buses")
data class BusEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val busNumber: String,
    val plateNumber: String,
    val capacity: Int,
    val driverId: String?,
    val driverName: String?,
    val driverPhone: String? = "+91 98765 43210",
    val driverPhoto: String = "avatar_driver_1",
    val status: String = "ON_THE_WAY", // "ON_THE_WAY", "DELAYED", "EMERGENCY", "IDLE", "MAINTENANCE"
    val routeName: String,
    val speedKmH: Int = 45,
    val lat: Double = 28.6139,
    val lng: Double = 77.2090,
    val fuelLevelPercent: Int = 85,
    val gpsSource: String = "Driver Mobile GPS", // "Driver Mobile GPS", "OBD-II Sensor", "Hardware GPS"
    val isActive: Boolean = true,
    val routeProgress: Float = 0.65f, // 0.0f to 1.0f
    val etaMinutes: Int = 12,
    val studentsPickedUp: Int = 24,
    val studentsRemaining: Int = 8,
    val delayStatusColor: String = "GREEN" // "GREEN", "YELLOW", "RED"
)

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val name: String,
    val phone: String,
    val licenseNumber: String,
    val assignedBusId: String?,
    val status: String = "ON_DUTY", // "ON_DUTY", "OFF_DUTY", "IN_TRANSIT"
    val rating: Float = 4.8f,
    val experience: String = "6 Years",
    val photo: String = "avatar_driver_1"
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val name: String,
    val grade: String,
    val section: String = "A",
    val parentName: String = "Parent Name",
    val parentPhone: String,
    val homeAddress: String = "House #12, Sector 4, Vasant Kunj",
    val gpsLocation: String = "28.5244, 77.1855",
    val stopName: String,
    val busId: String?,
    val pickupOrder: Int = 1,
    val specialNotes: String = "Front seat preference",
    val pickupStatus: String = "PENDING", // "PENDING", "PICKED_UP", "SKIPPED"
    val pickupTime: String? = null,
    val photo: String = "avatar_student_1"
)

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val routeName: String,
    val startLocation: String,
    val endLocation: String,
    val totalStops: Int,
    val estimatedTimeMinutes: Int,
    val status: String // "ACTIVE", "COMPLETED", "SCHEDULED"
)

@Entity(tableName = "ai_alerts")
data class AiAlertEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val title: String,
    val message: String,
    val severity: String, // "HIGH", "MEDIUM", "LOW"
    val timestamp: Long,
    val isResolved: Boolean = false,
    val busId: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val type: String, // "BUS_STARTED", "BUS_DELAYED", "WRONG_ROUTE", "EMERGENCY", "SUBSCRIPTION_WARNING"
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: String,
    val schoolId: String,
    val role: String,
    val userName: String,
    val userEmail: String,
    val token: String,
    val isLoggedIn: Boolean,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

