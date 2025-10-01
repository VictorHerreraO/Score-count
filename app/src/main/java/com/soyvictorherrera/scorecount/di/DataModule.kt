package com.soyvictorherrera.scorecount.di

import android.content.Context
import android.content.SharedPreferences
import com.soyvictorherrera.scorecount.data.datasource.LocalScoreDataSource
import com.soyvictorherrera.scorecount.data.datasource.SettingsLocalDataSource
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
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("score_count_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSettingsLocalDataSource(sharedPreferences: SharedPreferences): SettingsLocalDataSource {
        return SettingsLocalDataSource(sharedPreferences)
    }
}
