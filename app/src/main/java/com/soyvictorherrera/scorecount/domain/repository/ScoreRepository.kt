package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getGameState(): Flow<GameState>
    suspend fun incrementScore(playerId: Int, isDeuce: Boolean)
    suspend fun decrementScore(playerId: Int)
    suspend fun manualSwitchServe() // Renamed from switchServe()
    suspend fun resetGame()
    suspend fun undoLastAction() // Basic undo, might need more complex state management
}
