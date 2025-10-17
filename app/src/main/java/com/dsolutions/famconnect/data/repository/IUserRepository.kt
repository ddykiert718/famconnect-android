package com.dsolutions.famconnect.data.repository

import com.dsolutions.famconnect.model.Family
import com.dsolutions.famconnect.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface IUserRepository {
    fun getCurrentUser(): FirebaseUser?
    suspend fun getUserData(userId: String): User?
    suspend fun getFamilyData(familyId: String): Family?
    suspend fun addUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun createUserWithEmailAndPassword(email: String, password: String) : FirebaseUser
    suspend fun joinFamily(familyId: String, familyPin: String): String
    suspend fun createFamily(familyName: String, familyPin: String): String
}
