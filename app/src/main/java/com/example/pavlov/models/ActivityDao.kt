package com.example.pavlov.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActivity(activity: Activity)

    @Query("SELECT * FROM activity_log " +
           "WHERE strftime('%Y-%m-%d',completion_timestamp) = strftime('%Y-%m-%d', 'now', 'localtime')"
    )
    fun getAllActivitiesCompletedToday(): Flow<List<Activity>>
}