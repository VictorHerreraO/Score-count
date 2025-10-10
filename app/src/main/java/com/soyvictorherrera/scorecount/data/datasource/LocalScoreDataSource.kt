package com.soyvictorherrera.scorecount.data.datasource

import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for game state.
 * This is a "dumb" component responsible only for holding and emitting the current GameState.
 * It contains NO business logic - all game rules are in the domain layer (ScoreCalculator).
 */
@Singleton
class LocalScoreDataSource @Inject constructor() {

    // Initial default state
    private val initialPlayer1 = Player(id = 1, name = "Player 1", score = 0)
    private val initialPlayer2 = Player(id = 2, name = "Player 2", score = 0)
    private val initialGameState = GameState(
        player1 = initialPlayer1,
        player2 = initialPlayer2,
        servingPlayerId = initialPlayer1.id,
        player1SetsWon = 0,
        player2SetsWon = 0,
        isDeuce = false,
        isFinished = false
    )

    private val _gameState = MutableStateFlow(initialGameState)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    /**
     * Update the game state with a new state.
     * This is the only write method - all state mutations go through here.
     */
    fun updateState(newState: GameState) {
        _gameState.value = newState
    }
}
