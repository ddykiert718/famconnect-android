package com.dsolutions.famconnect.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "families")
data class FamilyEntity(
    @PrimaryKey(autoGenerate = true) var familyId: Long = 0,
    val remoteId: String? = null, // Firestore ID
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
    //val memberIds: List<String> = emptyList()  // User-IDs, keine Objekte
)