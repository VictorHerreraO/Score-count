package com.soyvictorherrera.scorecount.ui.main

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @BeforeEach
    fun setUp() {
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = MainViewModel(settingsRepository = fakeSettingsRepository)
    }

    @Test
    fun `viewModel exposes settings from repository`() = runTest {
        // Given
        val expectedSettings = GameSettings(
            pointsToWinSet = 21,
            numberOfSets = 3,
            showTitle = false,
            keepScreenOn = true
        )
        fakeSettingsRepository.setSettings(expectedSettings)

        // When
        val actualSettings = viewModel.settings.value

        // Then
        assertEquals(expectedSettings, actualSettings)
    }

    @Test
    fun `viewModel settings updates when repository updates`() = runTest {
        // Given - Initial settings
        val initialSettings = GameSettings(pointsToWinSet = 11)
        fakeSettingsRepository.setSettings(initialSettings)
        assertEquals(initialSettings, viewModel.settings.value)

        // When - Repository updates
        val updatedSettings = GameSettings(pointsToWinSet = 21)
        fakeSettingsRepository.setSettings(updatedSettings)

        // Then - ViewModel exposes updated settings
        assertEquals(updatedSettings, viewModel.settings.value)
    }

    @Test
    fun `viewModel exposes default settings on initialization`() = runTest {
        // When
        val settings = viewModel.settings.value

        // Then - Should have default GameSettings values
        assertEquals(GameSettings(), settings)
    }

    // --- Fake Repository ---

    class FakeSettingsRepository : SettingsRepository {
        private val _settings = MutableStateFlow(GameSettings())

        override fun getSettings(): StateFlow<GameSettings> = _settings

        override suspend fun saveSettings(settings: GameSettings) {
            _settings.value = settings
        }

        fun setSettings(settings: GameSettings) {
            _settings.value = settings
        }
    }
}
