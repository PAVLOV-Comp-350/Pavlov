package com.example.pavlov.models

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    version = 5,
    entities = [
        Goal::class,
        Activity::class,
        PetEquip::class,
        PetAccessoryTransactionRecord::class
   ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
        AutoMigration (from = 3, to = 4),
        AutoMigration (from = 4, to = 5),
    ],
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val goalDao: GoalDao
    abstract val activityDao: ActivityDao
    abstract val petDao: PetDao
}