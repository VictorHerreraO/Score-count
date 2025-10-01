package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<GameSettings>
    suspend fun saveSettings(settings: GameSettings)
}
