package com.dsolutions.famconnect.di

import com.dsolutions.famconnect.data.repository.IEventRepository
import com.dsolutions.famconnect.data.repository.IUserRepository
import com.dsolutions.famconnect.data.repository.wrapper.EventRepository
import com.dsolutions.famconnect.data.repository.wrapper.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEventRepository(
    ): IEventRepository {
        return EventRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
    ): IUserRepository {
        return UserRepository()
    }

}