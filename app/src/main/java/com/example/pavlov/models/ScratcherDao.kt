package com.example.pavlov.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScratcherDao {
    @Insert
    suspend fun insert(scratcher: Scratcher)

    @Query("SELECT * FROM scratchers ORDER BY creation_timestamp DESC")
    suspend fun getAllScratchers(): List<Scratcher>

    @Query("DELETE FROM scratchers")
    suspend fun deleteAll()
}
