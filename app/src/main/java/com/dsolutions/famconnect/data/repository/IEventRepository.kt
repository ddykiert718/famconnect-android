package com.dsolutions.famconnect.data.repository

import com.dsolutions.famconnect.data.entities.EventEntity
import com.dsolutions.famconnect.model.Event
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
    suspend fun addEvent(event: Event)
    suspend fun updateEvent(event: Event)
    fun getEventsForFamilyFlow(familyId: String): Flow<List<Event>>
    suspend fun deleteEvent(event: Event)
}
