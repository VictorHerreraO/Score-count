package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.SettingsLocalDataSource
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getSettings(): StateFlow<GameSettings> {
        return localDataSource.settings
    }

    override suspend fun saveSettings(settings: GameSettings) {
        localDataSource.saveSettings(settings)
    }
}
