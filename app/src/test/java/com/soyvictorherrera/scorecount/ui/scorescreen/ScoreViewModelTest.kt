package com.soyvictorherrera.scorecount.ui.scorescreen

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class ScoreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ScoreViewModel
    private lateinit var fakeScoreRepository: FakeScoreRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var fakeMatchRepository: FakeMatchRepository
    private lateinit var saveMatchUseCase: SaveMatchUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeScoreRepository = FakeScoreRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        fakeMatchRepository = FakeMatchRepository()

        // Create use cases
        val incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
        val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository)
        val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
        val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
        saveMatchUseCase = SaveMatchUseCase(fakeMatchRepository)

        viewModel = ScoreViewModel(
            scoreRepository = fakeScoreRepository,
            incrementScoreUseCase = incrementScoreUseCase,
            decrementScoreUseCase = decrementScoreUseCase,
            manualSwitchServeUseCase = manualSwitchServeUseCase,
            resetGameUseCase = resetGameUseCase,
            saveMatchUseCase = saveMatchUseCase,
            settingsRepository = fakeSettingsRepository
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel exposes gameState from repository`() = runTest {
        // Given
        val expectedState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 5),
            player2 = Player(id = 2, name = "Bob", score = 3),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(expectedState)

        // When
        val actualState = viewModel.gameState.first()

        // Then
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `viewModel exposes gameSettings from repository`() = runTest {
        // Given
        val expectedSettings = GameSettings(
            pointsToWinSet = 21,
            numberOfSets = 3,
            showTitle = false
        )
        fakeSettingsRepository.setSettings(expectedSettings)

        // When
        val actualSettings = viewModel.gameSettings.first()

        // Then
        assertEquals(expectedSettings, actualSettings)
    }

    @Test
    fun `incrementScore delegates to IncrementScoreUseCase`() = runTest {
        // Given
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 0),
            player2 = Player(id = 2, name = "Bob", score = 0),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        viewModel.incrementScore(playerId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val newState = viewModel.gameState.first()
        assertEquals(1, newState.player1.score)
        assertEquals(0, newState.player2.score)
    }

    @Test
    fun `decrementScore delegates to DecrementScoreUseCase`() = runTest {
        // Given
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 5),
            player2 = Player(id = 2, name = "Bob", score = 3),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        viewModel.decrementScore(playerId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val newState = viewModel.gameState.first()
        assertEquals(4, newState.player1.score)
        assertEquals(3, newState.player2.score)
    }

    @Test
    fun `manualSwitchServe delegates to ManualSwitchServeUseCase`() = runTest {
        // Given
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 0),
            player2 = Player(id = 2, name = "Bob", score = 0),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        viewModel.manualSwitchServe()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val newState = viewModel.gameState.first()
        assertEquals(2, newState.servingPlayerId)
    }

    @Test
    fun `resetGame delegates to ResetGameUseCase with winner from state`() = runTest {
        // Given - Player 1 has more sets won
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 10),
            player2 = Player(id = 2, name = "Bob", score = 8),
            servingPlayerId = 1,
            player1SetsWon = 3,
            player2SetsWon = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        viewModel.resetGame()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val newState = viewModel.gameState.first()
        assertEquals(0, newState.player1.score)
        assertEquals(0, newState.player2.score)
        assertEquals(0, newState.player1SetsWon)
        assertEquals(0, newState.player2SetsWon)
    }

    @Test
    fun `auto-saves match when game finishes`() = runTest {
        // Given - Set up initial state (not finished)
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 10),
            player2 = Player(id = 2, name = "Bob", score = 5),
            servingPlayerId = 1,
            player1SetsWon = 2,
            player2SetsWon = 1,
            isFinished = false
        )
        fakeScoreRepository.setState(initialState)

        // Re-create ViewModel to trigger init block
        val incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
        val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository)
        val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
        val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
        val newSaveMatchUseCase = SaveMatchUseCase(fakeMatchRepository)

        viewModel = ScoreViewModel(
            scoreRepository = fakeScoreRepository,
            incrementScoreUseCase = incrementScoreUseCase,
            decrementScoreUseCase = decrementScoreUseCase,
            manualSwitchServeUseCase = manualSwitchServeUseCase,
            resetGameUseCase = resetGameUseCase,
            saveMatchUseCase = newSaveMatchUseCase,
            settingsRepository = fakeSettingsRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // When - Change state to finished
        val finishedState = initialState.copy(isFinished = true)
        fakeScoreRepository.setState(finishedState)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Match should be auto-saved
        val savedMatches = fakeMatchRepository.getMatchList().first()
        assertEquals(1, savedMatches.size)
        val savedMatch = savedMatches.first()
        assertEquals("Alice", savedMatch.players.first.name)
        assertEquals("Bob", savedMatch.players.second.name)
        assertEquals(2, savedMatch.score.first)
        assertEquals(1, savedMatch.score.second)
    }

    @Test
    fun `does not auto-save match when game is already finished`() = runTest {
        // Given - Game starts already finished
        val finishedState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 0),
            player2 = Player(id = 2, name = "Bob", score = 0),
            servingPlayerId = 1,
            player1SetsWon = 3,
            player2SetsWon = 0,
            isFinished = true
        )
        fakeScoreRepository.setState(finishedState)

        // Re-create ViewModel
        val incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
        val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository)
        val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
        val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
        val newSaveMatchUseCase = SaveMatchUseCase(fakeMatchRepository)

        viewModel = ScoreViewModel(
            scoreRepository = fakeScoreRepository,
            incrementScoreUseCase = incrementScoreUseCase,
            decrementScoreUseCase = decrementScoreUseCase,
            manualSwitchServeUseCase = manualSwitchServeUseCase,
            resetGameUseCase = resetGameUseCase,
            saveMatchUseCase = newSaveMatchUseCase,
            settingsRepository = fakeSettingsRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // When - State remains finished (no transition from false to true)
        // Do nothing, just let init block run

        testDispatcher.scheduler.advanceUntilIdle()

        // Then - No match should be saved (because there was no transition)
        val savedMatches = fakeMatchRepository.getMatchList().first()
        assertEquals(0, savedMatches.size)
    }

    // --- Fake Repositories ---

    class FakeScoreRepository : ScoreRepository {
        private val _gameState = MutableStateFlow(
            GameState(
                player1 = Player(id = 1, name = "Player 1", score = 0),
                player2 = Player(id = 2, name = "Player 2", score = 0),
                servingPlayerId = 1
            )
        )

        override fun getGameState(): StateFlow<GameState> = _gameState

        override suspend fun updateGameState(newState: GameState) {
            _gameState.value = newState
        }

        fun setState(state: GameState) {
            _gameState.value = state
        }
    }

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

    class FakeMatchRepository : com.soyvictorherrera.scorecount.domain.repository.MatchRepository {
        private val _matches = MutableStateFlow<List<Match>>(emptyList())

        override fun getMatchList(): Flow<List<Match>> = _matches

        override suspend fun saveMatch(match: Match) {
            _matches.value = _matches.value + match
        }
    }
}
