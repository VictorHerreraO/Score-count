package com.soyvictorherrera.scorecount.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingItemData {
    data class ToggleItem(
        val text: String,
        val icon: ImageVector,
        val isChecked: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItemData()

    data class ActionItem(
        val text: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    ) : SettingItemData()

    data class StepperItem(
        val text: String,
        val subtitle: String? = null,
        val icon: ImageVector,
        val value: Int,
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit,
        val valueRange: IntRange
    ) : SettingItemData()

    data class SwitchSetting(
        val text: String,
        val subtitle: String? = null,
        val icon: ImageVector,
        val isChecked: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItemData()
}

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

    fun updateKeepScreenOn(keepOn: Boolean) {
        _settings.value = _settings.value.copy(keepScreenOn = keepOn)
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            settingsRepository.saveSettings(_settings.value)
        }
    }

    fun getGameControls(): List<SettingItemData> {
        val currentSettings = _settings.value
        return listOf(
            SettingItemData.ToggleItem("Show title", Icons.Filled.Title, currentSettings.showTitle) { updateShowTitle(it) },
            SettingItemData.ToggleItem("Show names", Icons.Filled.Badge, currentSettings.showNames) { updateShowNames(it) },
            SettingItemData.ToggleItem("Show sets", Icons.Filled.CalendarToday, currentSettings.showSets) { updateShowSets(it) },
            SettingItemData.ToggleItem("Mark serve", Icons.Filled.PersonSearch, currentSettings.markServe) { updateMarkServe(it) },
            SettingItemData.ToggleItem("Mark deuce", Icons.Filled.Info, currentSettings.markDeuce) { updateMarkDeuce(it) },
            SettingItemData.ToggleItem("Keep screen on", Icons.Filled.ScreenLockPortrait, currentSettings.keepScreenOn) { updateKeepScreenOn(it) }
        )
    }

    fun getTableTennisRules(): List<SettingItemData> {
        val currentSettings = _settings.value
        return listOf(
            SettingItemData.StepperItem(
                text = "Set to",
                subtitle = "Win by 2",
                icon = Icons.Filled.EmojiEvents,
                value = currentSettings.pointsToWinSet,
                onIncrement = { updatePointsToWinSet(currentSettings.pointsToWinSet + 1) },
                onDecrement = { updatePointsToWinSet(currentSettings.pointsToWinSet - 1) },
                valueRange = 1..100
            ),
            SettingItemData.StepperItem(
                text = "Match",
                subtitle = "Best of ${currentSettings.numberOfSets} sets",
                icon = Icons.Filled.MilitaryTech,
                value = currentSettings.numberOfSets,
                onIncrement = { updateNumberOfSets(currentSettings.numberOfSets + 2) },
                onDecrement = { updateNumberOfSets(currentSettings.numberOfSets - 2) },
                valueRange = 1..20
            ),
            SettingItemData.StepperItem(
                text = "Serve rotation after",
                subtitle = "1 after deuce",
                icon = Icons.AutoMirrored.Filled.RotateRight,
                value = currentSettings.serveRotationAfterPoints,
                onIncrement = { updateServeRotationAfterPoints(currentSettings.serveRotationAfterPoints + 1) },
                onDecrement = { updateServeRotationAfterPoints(currentSettings.serveRotationAfterPoints - 1) },
                valueRange = 1..10
            ),
            SettingItemData.SwitchSetting(
                text = "Winner serves",
                subtitle = "The winner of a game serves first in the next game",
                icon = Icons.Filled.Person,
                isChecked = currentSettings.winnerServesNextGame,
                onToggle = { updateWinnerServesNextGame(it) }
            )
        )
    }
}
