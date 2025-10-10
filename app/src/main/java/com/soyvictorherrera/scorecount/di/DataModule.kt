package com.soyvictorherrera.scorecount.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.data.database.AppDatabase
import com.soyvictorherrera.scorecount.data.database.dao.MatchDao
import com.soyvictorherrera.scorecount.data.datasource.gameStateDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStore instance for settings (created via delegate)
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "score-count-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMatchDao(database: AppDatabase): MatchDao {
        return database.matchDao()
    }

    @Provides
    @Singleton
    fun provideGameStateDataStore(@ApplicationContext context: Context): DataStore<GameStateProto> {
        return context.gameStateDataStore
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingsDataStore
    }
}
