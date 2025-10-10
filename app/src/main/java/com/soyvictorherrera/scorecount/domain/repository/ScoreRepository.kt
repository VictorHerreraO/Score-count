package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing game state.
 * This is a simplified interface that only handles state storage and retrieval.
 * Business logic is handled in the domain layer (ScoreCalculator and UseCases).
 */
interface ScoreRepository {
    /**
     * Get the current game state as a Flow.
     */
    fun getGameState(): Flow<GameState>

    /**
     * Update the game state with a new state.
     * This is the single write method - all state changes go through here.
     */
    suspend fun updateGameState(newState: GameState)
}
