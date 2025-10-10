package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing game settings.
 * Settings are state - there's always a current configuration.
 */
interface SettingsRepository {
    /**
     * Get the current settings as a StateFlow.
     * StateFlow guarantees there's always a current value.
     */
    fun getSettings(): StateFlow<GameSettings>

    suspend fun saveSettings(settings: GameSettings)
}
