package com.example.pavlov.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * This is the API to access the Goals table stored by SQLite.
 * The functions are suspend because they should be executed in a coroutine scope to enable
 * asynchronous io operations
 */
@Dao
interface GoalDao {
    @Upsert
    suspend fun addOrUpdateGoal(goal: Goal)

    @Delete
    suspend fun removeGoal(goal: Goal)

    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<Goal>>
}