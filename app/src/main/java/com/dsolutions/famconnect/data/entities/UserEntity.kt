package com.dsolutions.famconnect.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) var userId: Long = 0,
    val name: String,
    val email: String,
    val familyId: String
)
