package com.example.synoptrack.di

import android.content.Context
import androidx.room.Room
import com.example.synoptrack.auth.data.repository.AuthRepositoryImpl
import com.example.synoptrack.profile.data.repository.ProfileRepositoryImpl
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.core.database.SynopDatabase
import com.example.synoptrack.core.database.dao.ChatMessageDao
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import com.example.synoptrack.social.data.repository.ChatRepositoryImpl
import com.example.synoptrack.social.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository {
        return impl
    }


    @Provides
    @Singleton
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SynopDatabase {
        return Room.databaseBuilder(
            context,
            SynopDatabase::class.java,
            "synop_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(db: SynopDatabase): ChatMessageDao {
        return db.chatMessageDao()
    }
}