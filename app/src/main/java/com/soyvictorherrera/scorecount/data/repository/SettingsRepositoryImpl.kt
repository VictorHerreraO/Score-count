package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.SettingsLocalDataSource
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getSettings(): Flow<GameSettings> {
        return localDataSource.getSettings()
    }

    override suspend fun saveSettings(settings: GameSettings) {
        localDataSource.saveSettings(settings)
    }
}
