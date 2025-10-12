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

@ExperimentalCoroutinesApi
class IncrementScoreUseCaseTest {

    private lateinit var useCase: IncrementScoreUseCase
    private lateinit var fakeScoreRepository: FakeScoreRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @BeforeEach
    fun setUp() {
        fakeScoreRepository = FakeScoreRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        useCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
    }

    @Test
    fun `execute increments player 1 score from 0-0 to 1-0`() = runTest {
        // Given
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 0),
            player2 = Player(id = 2, name = "Bob", score = 0),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        useCase(playerId = 1)

        // Then
        val newState = fakeScoreRepository.getGameState().value
        assertEquals(1, newState.player1.score)
        assertEquals(0, newState.player2.score)
    }

    @Test
    fun `execute increments player 2 score from 0-0 to 0-1`() = runTest {
        // Given
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 0),
            player2 = Player(id = 2, name = "Bob", score = 0),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        useCase(playerId = 2)

        // Then
        val newState = fakeScoreRepository.getGameState().value
        assertEquals(0, newState.player1.score)
        assertEquals(1, newState.player2.score)
    }

    @Test
    fun `execute completes set when reaching points to win`() = runTest {
        // Given - Player 1 at 10 points, needs 11 to win set
        val settings = GameSettings(pointsToWinSet = 11, numberOfSets = 3)
        fakeSettingsRepository.setSettings(settings)

        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 10),
            player2 = Player(id = 2, name = "Bob", score = 5),
            servingPlayerId = 1,
            player1SetsWon = 0,
            player2SetsWon = 0
        )
        fakeScoreRepository.setState(initialState)

        // When
        useCase(playerId = 1)

        // Then
        val newState = fakeScoreRepository.getGameState().value
        assertEquals(0, newState.player1.score) // Score resets after set win
        assertEquals(0, newState.player2.score)
        assertEquals(1, newState.player1SetsWon) // Player 1 wins the set
        assertEquals(0, newState.player2SetsWon)
    }

    @Test
    fun `execute updates repository state`() = runTest {
        // Given
        val initialState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 5),
            player2 = Player(id = 2, name = "Bob", score = 3),
            servingPlayerId = 1
        )
        fakeScoreRepository.setState(initialState)

        // When
        useCase(playerId = 1)

        // Then - Verify repository was updated
        val savedState = fakeScoreRepository.lastSavedState
        assertEquals(6, savedState?.player1?.score)
        assertEquals(3, savedState?.player2?.score)
    }
}
