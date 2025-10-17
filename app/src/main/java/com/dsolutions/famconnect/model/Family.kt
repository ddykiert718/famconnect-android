package com.dsolutions.famconnect.model

import com.google.firebase.firestore.Exclude

// Firestore-Pfad: /families/{familyId}
data class Family(
    val id: String = "",
    val name: String = "",
    val pin: String = "",
    val ownerId: String = "",

    @get: Exclude
    val users: List<User> = emptyList(),
    @get: Exclude
    val events: List<Event> = emptyList(),
)