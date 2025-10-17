package com.dsolutions.famconnect.model

import com.google.firebase.firestore.Exclude

// Firestore-Pfad: /users/{userId}
data class User(
    var id: String = "",
    var familyId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var gender: String = "",
    var alias: String = "",
    var role: String = "",
    var birthday: String = "",
) {
    @get:Exclude
    var familyPin: String? = null
}