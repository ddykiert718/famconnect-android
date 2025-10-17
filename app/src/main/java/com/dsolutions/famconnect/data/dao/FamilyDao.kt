package com.dsolutions.famconnect.data.dao

import androidx.room.*
import com.dsolutions.famconnect.data.entities.FamilyEntity

@Dao
interface FamilyDao {
    @Query("SELECT * FROM families WHERE familyId = :familyId")
    suspend fun getFamilyById(familyId: String): FamilyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: FamilyEntity)

    @Delete
    suspend fun deleteFamily(family: FamilyEntity)

    @Query("DELETE FROM families WHERE familyId = :familyId")
    suspend fun deleteFamilyById(familyId: String)
}

