package com.soyvictorherrera.scorecount.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(GameSettings()) // Initialize with default settings
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collectLatest { loadedSettings ->
                _settings.value = loadedSettings
            }
        }
    }

    fun updateShowTitle(show: Boolean) {
        _settings.value = _settings.value.copy(showTitle = show)
        saveSettings()
    }

    fun updateShowNames(show: Boolean) {
        _settings.value = _settings.value.copy(showNames = show)
        saveSettings()
    }

    fun updateShowSets(show: Boolean) {
        _settings.value = _settings.value.copy(showSets = show)
        saveSettings()
    }

    fun updateMarkServe(mark: Boolean) {
        _settings.value = _settings.value.copy(markServe = mark)
        saveSettings()
    }

    fun updateMarkDeuce(mark: Boolean) {
        _settings.value = _settings.value.copy(markDeuce = mark)
        saveSettings()
    }

    fun updatePointsToWinSet(points: Int) {
        val newPoints = points.coerceIn(1, 99)
        if (_settings.value.pointsToWinSet != newPoints) {
            _settings.value = _settings.value.copy(pointsToWinSet = newPoints)
            saveSettings()
        }
    }

    fun updateWinByTwo(winByTwo: Boolean) {
        if (_settings.value.winByTwo != winByTwo) {
            _settings.value = _settings.value.copy(winByTwo = winByTwo)
            saveSettings()
        }
    }

    fun updateNumberOfSets(sets: Int) {
        val newSets = sets.coerceIn(1, 99) // Best of X, so X must be odd, or allow even?
        if (_settings.value.numberOfSets != newSets) {
            _settings.value = _settings.value.copy(numberOfSets = newSets)
            saveSettings()
        }
    }

    fun updateServeRotationAfterPoints(points: Int) {
        val newPoints = points.coerceIn(1, 99)
         if (_settings.value.serveRotationAfterPoints != newPoints) {
            _settings.value = _settings.value.copy(serveRotationAfterPoints = newPoints)
            saveSettings()
        }
    }

    fun updateServeChangeAfterDeuce(points: Int) {
        val newPoints = points.coerceIn(0, 99) // Allow 0 to disable
        if (_settings.value.serveChangeAfterDeuce != newPoints) {
            _settings.value = _settings.value.copy(serveChangeAfterDeuce = newPoints)
            saveSettings()
        }
    }

    fun updateWinnerServesNextGame(serves: Boolean) {
        _settings.value = _settings.value.copy(winnerServesNextGame = serves)
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            settingsRepository.saveSettings(_settings.value)
        }
    }
}
