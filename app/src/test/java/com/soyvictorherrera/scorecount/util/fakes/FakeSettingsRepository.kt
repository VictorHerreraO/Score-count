package com.soyvictorherrera.scorecount.util.fakes

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake implementation of [SettingsRepository] for testing.
 *
 * This fake provides:
 * - Default game settings
 * - [getSavedSettings] to verify save operations
 * - [setSettings] and [emitSettings] for test setup
 * - [resetSavedSettings] for cleanup between tests
 */
class FakeSettingsRepository : SettingsRepository {
    private val _settingsFlow = MutableStateFlow(GameSettings())
    private var savedSettings: GameSettings? = null

    override fun getSettings(): StateFlow<GameSettings> = _settingsFlow.asStateFlow()

    override suspend fun saveSettings(settings: GameSettings) {
        this.savedSettings = settings
        // Also emit to the flow so subscribers see the change, simulating real behavior
        _settingsFlow.value = settings
    }

    /**
     * Returns the last settings saved via [saveSettings].
     * Useful for verifying that settings were correctly persisted.
     */
    fun getSavedSettings(): GameSettings? = savedSettings

    /**
     * Helper method for test setup (alias for [emitSettings]).
     * Sets the current settings without tracking them in [savedSettings].
     */
    fun setSettings(settings: GameSettings) {
        _settingsFlow.value = settings
    }

    /**
     * Helper method for test setup.
     * Simulates new settings loaded from persistence.
     */
    fun emitSettings(settings: GameSettings) {
        _settingsFlow.value = settings
    }

    /**
     * Helper to reset saved settings for verifying "no save" scenarios.
     */
    fun resetSavedSettings() {
        savedSettings = null
    }
}
