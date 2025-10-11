package com.soyvictorherrera.scorecount.ui.main

import androidx.lifecycle.ViewModel
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for MainActivity.
 * Exposes application-wide settings to the UI layer.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    // Directly expose StateFlow from repository - no need for intermediate copying
    val settings: StateFlow<GameSettings> = settingsRepository.getSettings()
}
