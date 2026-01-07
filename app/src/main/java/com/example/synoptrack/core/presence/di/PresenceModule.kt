package com.example.synoptrack.core.presence.di

import com.example.synoptrack.core.presence.data.repository.PresenceRepositoryImpl
import com.example.synoptrack.core.presence.domain.repository.PresenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PresenceModule {

    @Binds
    @Singleton
    abstract fun bindPresenceRepository(
        impl: PresenceRepositoryImpl
    ): PresenceRepository
}
