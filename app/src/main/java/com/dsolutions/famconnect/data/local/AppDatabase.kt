package com.dsolutions.famconnect.data.local

import com.dsolutions.famconnect.data.entities.UserEntity
import com.dsolutions.famconnect.data.entities.FamilyEntity
import com.dsolutions.famconnect.data.entities.EventEntity
import com.dsolutions.famconnect.data.dao.UserDao
import com.dsolutions.famconnect.data.dao.FamilyDao
import com.dsolutions.famconnect.data.dao.EventDao
import com.dsolutions.famconnect.data.converters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [UserEntity::class, FamilyEntity::class, EventEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun familyDao(): FamilyDao
    abstract fun eventDao(): EventDao
}

