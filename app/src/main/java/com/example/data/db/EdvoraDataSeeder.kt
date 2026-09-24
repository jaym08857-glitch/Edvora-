package com.example.data.db

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EdvoraDataSeeder {

    suspend fun seedInitialData(db: EdvoraDatabase) = withContext(Dispatchers.IO) {
        val existingSchools = db.schoolDao().getSchoolById("SCH-1001")
        if (existingSchools != null) return@withContext

        // 1. Super Admin User
        val superAdmin = UserEntity(
            id = "USR-SUPER-01",
            schoolId = "",
            role = "SUPER_ADMIN",
            name = "Edvora Super Admin",
            email = "admin@edvora.com",
            phone = "+91 99999 00000",
            passwordHash = "Password123!",
            isOtpAuth = false,
            twoFactorEnabled = true,
            twoFactorSecret = "123456"
        )
        db.userDao().insertUser(superAdmin)

        // 2. Demo School 1: Delhi Public School, Vasant Kunj
        val school1 = SchoolEntity(
            id = "SCH-1001",
            name = "Delhi Public School, Vasant Kunj",
            logoBadge = "DPS",
            address = "Sector C, Pocket 5, Vasant Kunj",
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
        db.schoolDao().insertSchool(school1)

        val school1Admin = UserEntity(
            id = "USR-DPS-01",
            schoolId = "SCH-1001",
            role = "SCHOOL_ADMIN",
            name = "Rajesh Verma",
            email = "admin@dps.edu.in",
            phone = "+91 98100 12345",
            passwordHash = "Password123!",
            isOtpAuth = false
        )
        db.userDao().insertUser(school1Admin)

        // Demo School 2: St. Xavier's High School, Mumbai
        val school2 = SchoolEntity(
            id = "SCH-1002",
            name = "St. Xavier's High School",
            logoBadge = "STX",
            address = "5, Mahapalika Marg, Dhobi Talao",
            city = "Mumbai",
            state = "Maharashtra",
            country = "India",
            principalName = "Fr. Francis D'Souza",
            transportManagerName = "Anil Kulkarni",
            email = "admin@stxaviers.edu.in",
            phone = "+91 98200 54321",
            totalStudents = 850,
            totalBuses = 16,
            subscriptionPlan = "MONTHLY",
            subscriptionExpiryTimestamp = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
        )
        db.schoolDao().insertSchool(school2)

        val school2Admin = UserEntity(
            id = "USR-STX-01",
            schoolId = "SCH-1002",
            role = "SCHOOL_ADMIN",
            name = "Anil Kulkarni",
            email = "admin@stxaviers.edu.in",
            phone = "+91 98200 54321",
            passwordHash = "Password123!",
            isOtpAuth = false
        )
        db.userDao().insertUser(school2Admin)

        // Demo School 3: Global International Academy (Expired Free Trial demo)
        val school3 = SchoolEntity(
            id = "SCH-1003",
            name = "Global International Academy",
            logoBadge = "GIA",
            address = "100 Feet Rd, Indiranagar",
            city = "Bengaluru",
            state = "Karnataka",
            country = "India",
            principalName = "Mrs. Kavita Rao",
            transportManagerName = "Suresh Reddy",
            email = "admin@gia.edu.in",
            phone = "+91 98450 99887",
            totalStudents = 450,
            totalBuses = 8,
            subscriptionPlan = "FREE_TRIAL",
            subscriptionExpiryTimestamp = System.currentTimeMillis() - (2L * 24 * 3600 * 1000) // Expired 2 days ago
        )
        db.schoolDao().insertSchool(school3)

        // 3. Driver Account
        val driverUser = UserEntity(
            id = "USR-DRV-01",
            schoolId = "SCH-1001",
            role = "DRIVER",
            name = "Ramesh Kumar",
            email = "ramesh.driver@dps.edu.in",
            phone = "+91 98765 43210",
            passwordHash = "Password123!",
            isOtpAuth = true,
            assignedBusId = "BUS-101"
        )
        db.userDao().insertUser(driverUser)

        val driverEntity = DriverEntity(
            id = "DRV-101",
            schoolId = "SCH-1001",
            name = "Ramesh Kumar",
            phone = "+91 98765 43210",
            licenseNumber = "DL-14201100987",
            assignedBusId = "BUS-101",
            status = "IN_TRANSIT",
            rating = 4.9f
        )
        db.driverDao().insertDriver(driverEntity)

        val driver2Entity = DriverEntity(
            id = "DRV-102",
            schoolId = "SCH-1001",
            name = "Gurpreet Singh",
            phone = "+91 98111 22334",
            licenseNumber = "DL-14201500123",
            assignedBusId = "BUS-102",
            status = "ON_DUTY",
            rating = 4.7f
        )
        db.driverDao().insertDriver(driver2Entity)

        // 4. Buses
        val bus1 = BusEntity(
            id = "BUS-101",
            schoolId = "SCH-1001",
            busNumber = "BUS-101",
            plateNumber = "DL 01 AB 1234",
            capacity = 42,
            driverId = "DRV-101",
            driverName = "Ramesh Kumar",
            status = "ON_THE_WAY",
            routeName = "Route 4A - Vasant Kunj to Saket",
            speedKmH = 48,
            lat = 28.5244,
            lng = 77.1855,
            fuelLevelPercent = 88
        )
        db.busDao().insertBus(bus1)

        val bus2 = BusEntity(
            id = "BUS-102",
            schoolId = "SCH-1001",
            busNumber = "BUS-102",
            plateNumber = "DL 01 CD 5678",
            capacity = 36,
            driverId = "DRV-102",
            driverName = "Gurpreet Singh",
            status = "IDLE",
            routeName = "Route 2B - R.K. Puram Express",
            speedKmH = 0,
            lat = 28.5583,
            lng = 77.1729,
            fuelLevelPercent = 72
        )
        db.busDao().insertBus(bus2)

        val bus3 = BusEntity(
            id = "BUS-103",
            schoolId = "SCH-1001",
            busNumber = "BUS-103",
            plateNumber = "DL 01 EF 9012",
            capacity = 50,
            driverId = null,
            driverName = "Unassigned",
            status = "MAINTENANCE",
            routeName = "Route 7C - Hauz Khas Metro",
            speedKmH = 0,
            lat = 28.5494,
            lng = 77.2001,
            fuelLevelPercent = 40
        )
        db.busDao().insertBus(bus3)

        // 5. Routes
        val route1 = RouteEntity(
            id = "RT-101",
            schoolId = "SCH-1001",
            routeName = "Route 4A - Vasant Kunj to Saket",
            startLocation = "Vasant Kunj C-Block",
            endLocation = "DPS Vasant Kunj Campus",
            totalStops = 8,
            estimatedTimeMinutes = 35,
            status = "ACTIVE"
        )
        db.routeDao().insertRoute(route1)

        val route2 = RouteEntity(
            id = "RT-102",
            schoolId = "SCH-1001",
            routeName = "Route 2B - R.K. Puram Express",
            startLocation = "R.K. Puram Sec 3",
            endLocation = "DPS Vasant Kunj Campus",
            totalStops = 6,
            estimatedTimeMinutes = 25,
            status = "SCHEDULED"
        )
        db.routeDao().insertRoute(route2)

        // 6. Students
        val students = listOf(
            StudentEntity(
                id = "STD-1001",
                schoolId = "SCH-1001",
                name = "Aarav Sharma",
                grade = "Class 8-A",
                stopName = "Vasant Kunj B-Block Metro Gate 2",
                busId = "BUS-101",
                parentPhone = "+91 98111 99887",
                pickupStatus = "PICKED_UP",
                pickupTime = "07:22 AM"
            ),
            StudentEntity(
                id = "STD-1002",
                schoolId = "SCH-1001",
                name = "Ananya Verma",
                grade = "Class 5-B",
                stopName = "Munirka DDA Flats Stop 4",
                busId = "BUS-101",
                parentPhone = "+91 98100 44332",
                pickupStatus = "PICKED_UP",
                pickupTime = "07:30 AM"
            ),
            StudentEntity(
                id = "STD-1003",
                schoolId = "SCH-1001",
                name = "Rohan Malhotra",
                grade = "Class 10-C",
                stopName = "JNU North Gate Bus Stop",
                busId = "BUS-101",
                parentPhone = "+91 98222 11009",
                pickupStatus = "SKIPPED",
                pickupTime = null
            ),
            StudentEntity(
                id = "STD-1004",
                schoolId = "SCH-1001",
                name = "Priya Nambiar",
                grade = "Class 7-A",
                stopName = "Saket D-Block Market",
                busId = "BUS-101",
                parentPhone = "+91 98450 12300",
                pickupStatus = "PENDING",
                pickupTime = null
            ),
            StudentEntity(
                id = "STD-1005",
                schoolId = "SCH-1001",
                name = "Vivaan Gupta",
                grade = "Class 6-D",
                stopName = "Vasant Vihar C-Block Park",
                busId = "BUS-102",
                parentPhone = "+91 98990 77665",
                pickupStatus = "PENDING",
                pickupTime = null
            )
        )
        students.forEach { db.studentDao().insertStudent(it) }

        // 7. AI Alerts
        val alerts = listOf(
            AiAlertEntity(
                id = "ALT-101",
                schoolId = "SCH-1001",
                title = "AI Speed Warning - Bus #101",
                message = "Bus #101 exceeded recommended speed limit (58 km/h in 40 km/h zone near Munirka Flyover).",
                severity = "HIGH",
                timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
                isResolved = false,
                busId = "BUS-101"
            ),
            AiAlertEntity(
                id = "ALT-102",
                schoolId = "SCH-1001",
                title = "Route Optimization Suggestion",
                message = "Traffic delay on Vasant Marg (18 min congestion). AI rerouted Bus #101 via Outer Ring Road to stay on schedule.",
                severity = "MEDIUM",
                timestamp = System.currentTimeMillis() - 42 * 60 * 1000,
                isResolved = true,
                busId = "BUS-101"
            ),
            AiAlertEntity(
                id = "ALT-103",
                schoolId = "SCH-1001",
                title = "Predictive Maintenance Alert",
                message = "Bus #103 brake pad telemetry indicates 82% wear. Service recommended before next morning trip.",
                severity = "MEDIUM",
                timestamp = System.currentTimeMillis() - 3 * 3600 * 1000,
                isResolved = false,
                busId = "BUS-103"
            )
        )
        alerts.forEach { db.aiAlertDao().insertAlert(it) }

        // 8. Notifications
        val notifications = listOf(
            NotificationEntity(
                id = "NOTIF-101",
                schoolId = "SCH-1001",
                type = "BUS_STARTED",
                title = "Bus #101 Started Route",
                message = "Ramesh Kumar initialized morning pickup route 4A.",
                timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
                isRead = false
            ),
            NotificationEntity(
                id = "NOTIF-102",
                schoolId = "SCH-1001",
                type = "BUS_DELAYED",
                title = "Slight Traffic Delay - Bus #102",
                message = "Bus #102 delayed by 7 mins near R.K. Puram flyover due to morning rush.",
                timestamp = System.currentTimeMillis() - 30 * 60 * 1000,
                isRead = false
            ),
            NotificationEntity(
                id = "NOTIF-103",
                schoolId = "SCH-1001",
                type = "WRONG_ROUTE",
                title = "Geofence Route Deviation",
                message = "Bus #101 took Outer Ring Road bypass (AI approved reroute).",
                timestamp = System.currentTimeMillis() - 20 * 60 * 1000,
                isRead = true
            ),
            NotificationEntity(
                id = "NOTIF-104",
                schoolId = "SCH-1001",
                type = "SUBSCRIPTION_WARNING",
                title = "Subscription Active",
                message = "Yearly Enterprise Plan active. Expiration in 352 days.",
                timestamp = System.currentTimeMillis() - 2 * 3600 * 1000,
                isRead = true
            )
        )
        notifications.forEach { db.notificationDao().insertNotification(it) }
    }
}
