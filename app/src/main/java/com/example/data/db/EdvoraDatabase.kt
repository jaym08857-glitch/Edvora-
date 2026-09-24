package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        SchoolEntity::class,
        UserEntity::class,
        BusEntity::class,
        DriverEntity::class,
        StudentEntity::class,
        RouteEntity::class,
        AiAlertEntity::class,
        NotificationEntity::class,
        SessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class EdvoraDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun userDao(): UserDao
    abstract fun busDao(): BusDao
    abstract fun driverDao(): DriverDao
    abstract fun studentDao(): StudentDao
    abstract fun routeDao(): RouteDao
    abstract fun aiAlertDao(): AiAlertDao
    abstract fun notificationDao(): NotificationDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: EdvoraDatabase? = null

        fun getInstance(context: Context): EdvoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EdvoraDatabase::class.java,
                    "edvora_enterprise.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
