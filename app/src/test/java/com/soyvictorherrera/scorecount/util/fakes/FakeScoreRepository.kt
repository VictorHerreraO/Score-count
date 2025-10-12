package com.soyvictorherrera.scorecount.util.fakes

import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Fake implementation of [ScoreRepository] for testing.
 *
 * This fake provides:
 * - A default initial state with two players at 0-0
 * - [lastSavedState] property to verify write operations
 * - [setState] helper method for test setup
 */
class FakeScoreRepository : ScoreRepository {
    private val _gameState = MutableStateFlow(
        GameState(
            player1 = Player(id = 1, name = "Player 1", score = 0),
            player2 = Player(id = 2, name = "Player 2", score = 0),
            servingPlayerId = 1
        )
    )

    /**
     * Tracks the last state saved via [updateGameState].
     * Useful for verifying that use cases correctly update the repository.
     */
    var lastSavedState: GameState? = null
        private set

    override fun getGameState(): StateFlow<GameState> = _gameState

    override suspend fun updateGameState(newState: GameState) {
        lastSavedState = newState
        _gameState.value = newState
    }

    /**
     * Helper method for test setup.
     * Sets the current game state without tracking it in [lastSavedState].
     */
    fun setState(state: GameState) {
        _gameState.value = state
    }
}
