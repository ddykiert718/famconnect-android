package com.dsolutions.famconnect.data.repository.wrapper

import com.dsolutions.famconnect.data.dao.FamilyDao
import com.dsolutions.famconnect.data.entities.FamilyEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FamilyRepository(
    private val familyDao: FamilyDao,
    private val firestore: FirebaseFirestore
) {
    private val familyCollection = firestore.collection("families")

    fun getFamily(familyId: String): Flow<FamilyEntity?> = flow {
        // Lokale Daten zuerst
        emit(familyDao.getFamilyById(familyId))

        // Dann aus Firestore abrufen
        val snapshot = familyCollection.document(familyId).get().await()
        val remoteFamily = snapshot.toObject(FamilyEntity::class.java)

        if (remoteFamily != null) {
            familyDao.insertFamily(remoteFamily)
            emit(remoteFamily)
        }
    }

    suspend fun addOrUpdateFamily(family: FamilyEntity) {
        // Lokal speichern
        familyDao.insertFamily(family)

        // Firestore aktualisieren
        familyCollection.document(family.familyId.toString()).set(family).await()
    }

    suspend fun deleteFamily(familyId: String) {
        familyDao.deleteFamilyById(familyId)
        familyCollection.document(familyId).delete().await()
    }
}

