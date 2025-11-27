package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.util.fakes.FakeScoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class UndoScoreUseCaseTest {
    private lateinit var useCase: UndoScoreUseCase
    private lateinit var fakeScoreRepository: FakeScoreRepository

    @BeforeEach
    fun setUp() {
        fakeScoreRepository = FakeScoreRepository()
        useCase = UndoScoreUseCase(fakeScoreRepository)
    }

    @Test
    fun `execute restores previous state after score change`() =
        runTest {
            // Given - Initial state and one score change
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)
            fakeScoreRepository.clearHistory() // Clear any history from setState

            val newState =
                initialState.copy(
                    player1 = initialState.player1.copy(score = 1)
                )
            fakeScoreRepository.updateGameState(newState)

            // When - Undo is called
            useCase()

            // Then - State is restored to initial
            val currentState = fakeScoreRepository.getGameState().value
            assertEquals(initialState, currentState)
        }

    @Test
    fun `execute handles multiple undo operations in sequence`() =
        runTest {
            // Given - Multiple state changes
            val state0 =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(state0)
            fakeScoreRepository.clearHistory()

            val state1 = state0.copy(player1 = state0.player1.copy(score = 1))
            fakeScoreRepository.updateGameState(state1)

            val state2 = state1.copy(player1 = state1.player1.copy(score = 2))
            fakeScoreRepository.updateGameState(state2)

            // When - Undo twice
            useCase()
            useCase()

            // Then - Back to state0
            val currentState = fakeScoreRepository.getGameState().value
            assertEquals(state0, currentState)
        }

    @Test
    fun `execute has no effect when history is empty`() =
        runTest {
            // Given - No history
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 5),
                    player2 = Player(id = 2, name = "Bob", score = 3),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)
            fakeScoreRepository.clearHistory()

            // When - Undo is called
            useCase()

            // Then - State unchanged
            val currentState = fakeScoreRepository.getGameState().value
            assertEquals(initialState, currentState)
        }

    @Test
    fun `hasUndoHistory returns true after state change`() =
        runTest {
            // Given - Initial state
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)
            fakeScoreRepository.clearHistory()

            assertFalse(fakeScoreRepository.hasUndoHistory().value)

            // When - State is updated
            val newState =
                initialState.copy(
                    player1 = initialState.player1.copy(score = 1)
                )
            fakeScoreRepository.updateGameState(newState)

            // Then - Has undo history
            assertTrue(fakeScoreRepository.hasUndoHistory().value)
        }

    @Test
    fun `hasUndoHistory returns false after all changes are undone`() =
        runTest {
            // Given - State change
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)
            fakeScoreRepository.clearHistory()

            val newState =
                initialState.copy(
                    player1 = initialState.player1.copy(score = 1)
                )
            fakeScoreRepository.updateGameState(newState)
            assertTrue(fakeScoreRepository.hasUndoHistory().value)

            // When - Undo all changes
            useCase()

            // Then - No undo history
            assertFalse(fakeScoreRepository.hasUndoHistory().value)
        }
}
