package com.ran.dicodingstory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ran.dicodingstory.data.local.room.StoryDatabase
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import com.ran.dicodingstory.data.local.source.userDataStore
import com.ran.dicodingstory.data.remote.retrofit.ApiConfig
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import com.ran.dicodingstory.data.remote.source.StoryRepository
import com.ran.dicodingstory.data.remote.source.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonInjection {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
        return applicationContext.userDataStore
    }

    @Provides
    @Singleton
    fun provideUserPrefRepository(dataStore: DataStore<Preferences>) : UserPreferencesRepository {
        return UserPreferencesRepository(dataStore)
    }
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }

    @Provides
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepository.getInstance(apiService)
    }

    @Provides
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase {
        return StoryDatabase.getDatabase(context)
    }

    @Provides
    fun provideStoryRepository(storyDatabase: StoryDatabase,userPreferencesRepository: UserPreferencesRepository, apiService: ApiService): StoryRepository {
        return StoryRepository.getInstance(storyDatabase,userPreferencesRepository, apiService)
    }
}