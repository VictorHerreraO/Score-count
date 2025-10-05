package com.soyvictorherrera.scorecount.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.soyvictorherrera.scorecount.data.datasource.LocalScoreDataSource
import com.soyvictorherrera.scorecount.data.datasource.settingsDataStore
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLocalScoreDataSource(settingsRepository: SettingsRepository): LocalScoreDataSource {
        return LocalScoreDataSource(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingsDataStore
    }
}
