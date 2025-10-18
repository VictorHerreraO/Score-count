package com.soyvictorherrera.scorecount.ui.scorescreen

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.usecase.*
import com.soyvictorherrera.scorecount.util.fakes.FakeMatchRepository
import com.soyvictorherrera.scorecount.util.fakes.FakeScoreRepository
import com.soyvictorherrera.scorecount.util.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
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
        val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
        val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
        val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
        saveMatchUseCase = SaveMatchUseCase(fakeMatchRepository)

        viewModel =
            ScoreViewModel(
                scoreRepository = fakeScoreRepository,
                incrementScoreUseCase = incrementScoreUseCase,
                decrementScoreUseCase = decrementScoreUseCase,
                manualSwitchServeUseCase = manualSwitchServeUseCase,
                resetGameUseCase = resetGameUseCase,
                saveMatchUseCase = saveMatchUseCase,
                settingsRepository = fakeSettingsRepository,
                dispatcher = testDispatcher
            )
        testDispatcher.scheduler.advanceUntilIdle() // Let init block complete
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel exposes gameState from repository`() =
        runTest {
            // Given
            val expectedState =
                GameState(
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
    fun `viewModel exposes gameSettings from repository`() =
        runTest {
            // Given
            val expectedSettings =
                GameSettings(
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
    fun `incrementScore delegates to IncrementScoreUseCase`() =
        runTest {
            // Given
            val initialState =
                GameState(
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
    fun `decrementScore delegates to DecrementScoreUseCase`() =
        runTest {
            // Given
            val initialState =
                GameState(
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
    fun `manualSwitchServe delegates to ManualSwitchServeUseCase`() =
        runTest {
            // Given
            val initialState =
                GameState(
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
    fun `resetGame delegates to ResetGameUseCase`() =
        runTest {
            // Given - Player 1 has more sets won
            val initialState =
                GameState(
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

            // Then - Game is reset (UseCase auto-determines winner internally)
            val newState = viewModel.gameState.first()
            assertEquals(0, newState.player1.score)
            assertEquals(0, newState.player2.score)
            assertEquals(0, newState.player1SetsWon)
            assertEquals(0, newState.player2SetsWon)
            // Winner determination happens in UseCase, not ViewModel
        }

    @Test
    fun `auto-saves match when game finishes`() =
        runTest {
            // Given - Set up initial state (not finished)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 10),
                    player2 = Player(id = 2, name = "Bob", score = 5),
                    servingPlayerId = 1,
                    player1SetsWon = 2,
                    player2SetsWon = 1,
                    isFinished = false
                )
            fakeScoreRepository.setState(initialState)
            testDispatcher.scheduler.advanceUntilIdle()

            // When - Change state to finished
            val finishedState = initialState.copy(isFinished = true)
            fakeScoreRepository.setState(finishedState)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Match should be auto-saved
            val savedMatches = fakeMatchRepository.getMatchList().first()
            assertEquals(1, savedMatches.size)
            val savedMatch = savedMatches.first()
            assertEquals("Alice", savedMatch.playerOneName)
            assertEquals("Bob", savedMatch.playerTwoName)
            assertEquals(2, savedMatch.playerOneScore)
            assertEquals(1, savedMatch.playerTwoScore)
        }

    @Test
    fun `does not auto-save match when game is already finished`() =
        runTest {
            // Given - Create a fresh match repository to avoid interference from setUp() ViewModel
            val isolatedMatchRepository = FakeMatchRepository()

            // Game starts already finished
            val finishedState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1,
                    player1SetsWon = 3,
                    player2SetsWon = 0,
                    isFinished = true
                )
            fakeScoreRepository.setState(finishedState)

            // Create a new ViewModel that will see the finished state as the first emission
            val incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
            val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
            val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
            val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val isolatedSaveMatchUseCase = SaveMatchUseCase(isolatedMatchRepository)

            @Suppress("UNUSED_VARIABLE")
            val isolatedViewModel =
                ScoreViewModel(
                    scoreRepository = fakeScoreRepository,
                    incrementScoreUseCase = incrementScoreUseCase,
                    decrementScoreUseCase = decrementScoreUseCase,
                    manualSwitchServeUseCase = manualSwitchServeUseCase,
                    resetGameUseCase = resetGameUseCase,
                    saveMatchUseCase = isolatedSaveMatchUseCase,
                    settingsRepository = fakeSettingsRepository,
                    dispatcher = testDispatcher
                )
            testDispatcher.scheduler.advanceUntilIdle()

            // When - State remains finished (no transition from false to true)
            // Do nothing, just let init block run

            testDispatcher.scheduler.advanceUntilIdle()

            // Then - No match should be saved to the isolated repository (because there was no transition)
            val savedMatches = isolatedMatchRepository.getMatchList().first()
            assertEquals(0, savedMatches.size)
        }
}
