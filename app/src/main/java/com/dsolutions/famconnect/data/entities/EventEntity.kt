package com.dsolutions.famconnect.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDate

@IgnoreExtraProperties
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val participants: List<String>, // User-IDs
    val location: String?,
    val repeat: String?,
    val notes: String?,
    val notification: String?,
    val createdBy: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val familyId: String,
    val isSynced: Boolean = false

)
