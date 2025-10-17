package com.dsolutions.famconnect.data.dao

import androidx.room.*
import com.dsolutions.famconnect.data.entities.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE familyId = :familyId")
    suspend fun getEventsForFamily(familyId: String): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: String)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("SELECT * FROM events WHERE isSynced = 0")
    suspend fun getUnsyncedEvents(): List<EventEntity>

    @Query("UPDATE events SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
