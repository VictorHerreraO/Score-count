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
 * - Undo history tracking matching production behavior
 */
class FakeScoreRepository : ScoreRepository {
    private val _gameState =
        MutableStateFlow(
            GameState(
                player1 = Player(id = 1, name = "Player 1", score = 0),
                player2 = Player(id = 2, name = "Player 2", score = 0),
                servingPlayerId = 1
            )
        )

    /**
     * In-memory history stack for undo functionality.
     * Limited to 50 states to match production behavior.
     */
    private val history = mutableListOf<GameState>()
    private val _hasUndoHistory = MutableStateFlow(false)

    /**
     * Tracks the last state saved via [updateGameState].
     * Useful for verifying that use cases correctly update the repository.
     */
    var lastSavedState: GameState? = null
        private set

    override fun getGameState(): StateFlow<GameState> = _gameState

    override suspend fun updateGameState(newState: GameState) {
        // Push current state to history
        history.add(_gameState.value)
        if (history.size > 50) {
            history.removeAt(0)
        }

        lastSavedState = newState
        _gameState.value = newState

        _hasUndoHistory.value = history.isNotEmpty()
    }

    override suspend fun undoLastChange() {
        if (history.isEmpty()) return

        val previousState = history.removeAt(history.lastIndex)
        _gameState.value = previousState
        lastSavedState = previousState

        _hasUndoHistory.value = history.isNotEmpty()
    }

    override fun hasUndoHistory(): StateFlow<Boolean> = _hasUndoHistory

    override suspend fun clearHistory() {
        history.clear()
        _hasUndoHistory.value = false
    }

    /**
     * Helper method for test setup.
     * Sets the current game state without tracking it in [lastSavedState].
     */
    fun setState(state: GameState) {
        _gameState.value = state
    }
}
