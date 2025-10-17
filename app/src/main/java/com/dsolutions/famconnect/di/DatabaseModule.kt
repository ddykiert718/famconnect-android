package com.dsolutions.famconnect.di

import android.content.Context
import androidx.room.Room
import com.dsolutions.famconnect.data.dao.EventDao
import com.dsolutions.famconnect.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "famconnect.db"
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }

    // Optional: Falls du auch UserDao oder FamilyDao brauchst
    // @Provides
    // fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
