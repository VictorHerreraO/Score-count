package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.util.fakes.FakeScoreRepository
import com.soyvictorherrera.scorecount.util.fakes.FakeSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Comprehensive tests for all score-related use cases:
 * - DecrementScoreUseCase
 * - ManualSwitchServeUseCase
 * - ResetGameUseCase
 */
@ExperimentalCoroutinesApi
class ScoreUseCasesTest {
    private lateinit var fakeScoreRepository: FakeScoreRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @BeforeEach
    fun setUp() {
        fakeScoreRepository = FakeScoreRepository()
        fakeSettingsRepository = FakeSettingsRepository()
    }

    // --- DecrementScoreUseCase Tests ---

    @Test
    fun `DecrementScoreUseCase decrements player 1 score from 5-3 to 4-3`() =
        runTest {
            // Given
            val useCase = DecrementScoreUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 5),
                    player2 = Player(id = 2, name = "Bob", score = 3),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase(playerId = 1)

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(4, newState.player1.score)
            assertEquals(3, newState.player2.score)
        }

    @Test
    fun `DecrementScoreUseCase decrements player 2 score from 3-7 to 3-6`() =
        runTest {
            // Given
            val useCase = DecrementScoreUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 3),
                    player2 = Player(id = 2, name = "Bob", score = 7),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase(playerId = 2)

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(3, newState.player1.score)
            assertEquals(6, newState.player2.score)
        }

    @Test
    fun `DecrementScoreUseCase does not go below 0`() =
        runTest {
            // Given
            val useCase = DecrementScoreUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase(playerId = 1)

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(0, newState.player1.score) // Should not go negative
            assertEquals(0, newState.player2.score)
        }

    @Test
    fun `DecrementScoreUseCase updates repository`() =
        runTest {
            // Given
            val useCase = DecrementScoreUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 10),
                    player2 = Player(id = 2, name = "Bob", score = 8),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase(playerId = 2)

            // Then
            val savedState = fakeScoreRepository.lastSavedState
            assertEquals(10, savedState?.player1?.score)
            assertEquals(7, savedState?.player2?.score)
        }

    // --- ManualSwitchServeUseCase Tests ---

    @Test
    fun `ManualSwitchServeUseCase switches serve from player 1 to player 2`() =
        runTest {
            // Given
            val useCase = ManualSwitchServeUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 5),
                    player2 = Player(id = 2, name = "Bob", score = 3),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase()

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(2, newState.servingPlayerId) // Serve switched to player 2
            assertEquals(5, newState.player1.score) // Scores unchanged
            assertEquals(3, newState.player2.score)
        }

    @Test
    fun `ManualSwitchServeUseCase switches serve from player 2 to player 1`() =
        runTest {
            // Given
            val useCase = ManualSwitchServeUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 7),
                    player2 = Player(id = 2, name = "Bob", score = 9),
                    servingPlayerId = 2
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase()

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(1, newState.servingPlayerId) // Serve switched to player 1
            assertEquals(7, newState.player1.score) // Scores unchanged
            assertEquals(9, newState.player2.score)
        }

    @Test
    fun `ManualSwitchServeUseCase updates repository`() =
        runTest {
            // Given
            val useCase = ManualSwitchServeUseCase(fakeScoreRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase()

            // Then
            val savedState = fakeScoreRepository.lastSavedState
            assertEquals(2, savedState?.servingPlayerId)
        }

    // --- ResetGameUseCase Tests ---

    @Test
    fun `ResetGameUseCase resets scores to 0-0`() =
        runTest {
            // Given
            val useCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 15),
                    player2 = Player(id = 2, name = "Bob", score = 12),
                    servingPlayerId = 1,
                    player1SetsWon = 2,
                    player2SetsWon = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase()

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(0, newState.player1.score)
            assertEquals(0, newState.player2.score)
            assertEquals(0, newState.player1SetsWon)
            assertEquals(0, newState.player2SetsWon)
            assertEquals(false, newState.isFinished)
        }

    @Test
    fun `ResetGameUseCase preserves player names`() =
        runTest {
            // Given
            val useCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Charlie", score = 10),
                    player2 = Player(id = 2, name = "Diana", score = 8),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase()

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals("Charlie", newState.player1.name)
            assertEquals("Diana", newState.player2.name)
        }

    @Test
    fun `ResetGameUseCase with last winner serves first if winnerServesNextGame is true`() =
        runTest {
            // Given
            val settings = GameSettings(winnerServesNextGame = true)
            fakeSettingsRepository.setSettings(settings)

            val useCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 10),
                    player2 = Player(id = 2, name = "Bob", score = 8),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When - Player 2 won last game
            useCase(lastGameWinnerId = 2)

            // Then
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(2, newState.servingPlayerId) // Winner serves
        }

    @Test
    fun `ResetGameUseCase with last winner does not serve if winnerServesNextGame is false`() =
        runTest {
            // Given
            val settings = GameSettings(winnerServesNextGame = false)
            fakeSettingsRepository.setSettings(settings)

            val useCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 10),
                    player2 = Player(id = 2, name = "Bob", score = 8),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When - Player 2 won last game
            useCase(lastGameWinnerId = 2)

            // Then - Always resets to Player 1 (initial state), not based on winner
            val newState = fakeScoreRepository.getGameState().value
            assertEquals(1, newState.servingPlayerId)
        }

    @Test
    fun `ResetGameUseCase updates repository`() =
        runTest {
            // Given
            val useCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 20),
                    player2 = Player(id = 2, name = "Bob", score = 18),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            useCase()

            // Then
            val savedState = fakeScoreRepository.lastSavedState
            assertEquals(0, savedState?.player1?.score)
            assertEquals(0, savedState?.player2?.score)
            assertEquals(0, savedState?.player1SetsWon)
            assertEquals(0, savedState?.player2SetsWon)
        }
}
