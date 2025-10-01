package com.soyvictorherrera.scorecount.data.datasource

import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// For a more robust undo, we'd need to store a history of states
// For now, let's assume a very simple "last state" for undo.
private var previousGameState: GameState? = null

// Initial default state
private val initialPlayer1 = Player(id = 1, name = "Player 1", score = 0)
private val initialPlayer2 = Player(id = 2, name = "Player 2", score = 0)
private val initialGameState = GameState(
    player1 = initialPlayer1,
    player2 = initialPlayer2,
    servingPlayerId = initialPlayer1.id,
    isFinished = false
)

class LocalScoreDataSource {

    private val _gameState = MutableStateFlow(initialGameState)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private fun updateState(newState: GameState) {
        previousGameState = _gameState.value
        _gameState.value = newState
    }

    fun incrementScore(playerId: Int) {
        val currentState = _gameState.value
        val newPlayer1 = if (playerId == currentState.player1.id) {
            currentState.player1.copy(score = currentState.player1.score + 1)
        } else {
            currentState.player1
        }
        val newPlayer2 = if (playerId == currentState.player2.id) {
            currentState.player2.copy(score = currentState.player2.score + 1)
        } else {
            currentState.player2
        }
        updateState(currentState.copy(player1 = newPlayer1, player2 = newPlayer2))
    }

    fun decrementScore(playerId: Int) {
        val currentState = _gameState.value
        val newPlayer1 = if (playerId == currentState.player1.id && currentState.player1.score > 0) {
            currentState.player1.copy(score = currentState.player1.score - 1)
        } else {
            currentState.player1
        }
        val newPlayer2 = if (playerId == currentState.player2.id && currentState.player2.score > 0) {
            currentState.player2.copy(score = currentState.player2.score - 1)
        } else {
            currentState.player2
        }
        updateState(currentState.copy(player1 = newPlayer1, player2 = newPlayer2))
    }

    fun switchServe() {
        val currentState = _gameState.value
        val newServingPlayerId = if (currentState.servingPlayerId == currentState.player1.id) currentState.player2.id else currentState.player1.id
        updateState(currentState.copy(servingPlayerId = newServingPlayerId))
    }

    fun resetGame() {
        updateState(initialGameState) // Reset to initial state
    }

    fun undoLastAction() {
        previousGameState?.let {
            _gameState.value = it // Restore previous state, direct update without saving current as previous
            previousGameState = null // Consume the undo state
        }
    }
}
