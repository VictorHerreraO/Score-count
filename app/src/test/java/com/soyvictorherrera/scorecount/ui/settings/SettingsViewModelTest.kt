package com.soyvictorherrera.scorecount.ui.settings

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    // Helper to get default settings for comparison
    private val defaultSettings = GameSettings()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = SettingsViewModel(fakeSettingsRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial settings are loaded from repository`() = runTest {
        val initialRepoSettings = GameSettings(pointsToWinSet = 21, showTitle = false)
        fakeSettingsRepository.emitSettings(initialRepoSettings)

        // Re-initialize ViewModel to trigger load with new emitted settings
        viewModel = SettingsViewModel(fakeSettingsRepository)
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

        assertEquals(initialRepoSettings, viewModel.settings.first())
    }

    @Test
    fun `updateShowTitle updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.showTitle

        viewModel.updateShowTitle(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.showTitle)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateShowNames updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.showNames

        viewModel.updateShowNames(newValue)
        testDispatcher.scheduler.advanceUntilIdle()


        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.showNames)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateShowSets updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.showSets

        viewModel.updateShowSets(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.showSets)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateMarkServe updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.markServe

        viewModel.updateMarkServe(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.markServe)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateMarkDeuce updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.markDeuce

        viewModel.updateMarkDeuce(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.markDeuce)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updatePointsToWinSet updates settings, coerces value, and saves`() = runTest {
        viewModel.updatePointsToWinSet(50)
        testDispatcher.scheduler.advanceUntilIdle()
        var updatedSettings = viewModel.settings.first()
        assertEquals(50, updatedSettings.pointsToWinSet)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (min)
        viewModel.updatePointsToWinSet(0)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(1, updatedSettings.pointsToWinSet)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (max - 99 as per ViewModel logic)
        viewModel.updatePointsToWinSet(150)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(99, updatedSettings.pointsToWinSet)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateWinByTwo updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.winByTwo

        viewModel.updateWinByTwo(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.winByTwo)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }


    @Test
    fun `updateNumberOfSets updates settings, coerces value, and saves`() = runTest {
        viewModel.updateNumberOfSets(5)
        testDispatcher.scheduler.advanceUntilIdle()
        var updatedSettings = viewModel.settings.first()
        assertEquals(5, updatedSettings.numberOfSets)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (min)
        viewModel.updateNumberOfSets(0)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(1, updatedSettings.numberOfSets)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (max - 99 as per ViewModel logic)
        viewModel.updateNumberOfSets(150)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(99, updatedSettings.numberOfSets)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateServeRotationAfterPoints updates settings, coerces value, and saves`() = runTest {
        viewModel.updateServeRotationAfterPoints(5)
        testDispatcher.scheduler.advanceUntilIdle()
        var updatedSettings = viewModel.settings.first()
        assertEquals(5, updatedSettings.serveRotationAfterPoints)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (min)
        viewModel.updateServeRotationAfterPoints(0)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(1, updatedSettings.serveRotationAfterPoints)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (max - 99)
        viewModel.updateServeRotationAfterPoints(150)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(99, updatedSettings.serveRotationAfterPoints)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateServeChangeAfterDeuce updates settings, coerces value, and saves`() = runTest {
        viewModel.updateServeChangeAfterDeuce(3)
        testDispatcher.scheduler.advanceUntilIdle()
        var updatedSettings = viewModel.settings.first()
        assertEquals(3, updatedSettings.serveChangeAfterDeuce)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (min - 0 is allowed)
        viewModel.updateServeChangeAfterDeuce(-5)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(0, updatedSettings.serveChangeAfterDeuce)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

        // Test coercion (max - 99)
        viewModel.updateServeChangeAfterDeuce(150)
        testDispatcher.scheduler.advanceUntilIdle()
        updatedSettings = viewModel.settings.first()
        assertEquals(99, updatedSettings.serveChangeAfterDeuce)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    @Test
    fun `updateWinnerServesNextGame updates settings and saves`() = runTest {
        val initialSettings = viewModel.settings.first()
        val newValue = !initialSettings.winnerServesNextGame

        viewModel.updateWinnerServesNextGame(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = viewModel.settings.first()
        assertEquals(newValue, updatedSettings.winnerServesNextGame)
        assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
    }

    // --- Fake Repository ---
    class FakeSettingsRepository : SettingsRepository {
        private val settingsFlow = MutableSharedFlow<GameSettings>(replay = 1)
        private var savedSettings: GameSettings? = null

        init {
            // Start with default settings so the flow has an initial value for the ViewModel's init
            settingsFlow.tryEmit(GameSettings())
        }

        override fun getSettings() = settingsFlow

        override suspend fun saveSettings(settings: GameSettings) {
            this.savedSettings = settings
            // Also emit to the flow so subscribers see the change, simulating real behavior
            settingsFlow.emit(settings)
        }

        // Helper for tests to check what was saved
        fun getSavedSettings(): GameSettings? = savedSettings

        // Helper for tests to simulate new settings loaded from persistence
        suspend fun emitSettings(settings: GameSettings) {
            settingsFlow.emit(settings)
        }

        // Helper to reset for verifying "no save" scenarios (if applicable)
        fun resetSavedSettings() {
            savedSettings = null
        }
    }
}
