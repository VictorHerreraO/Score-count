package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing game state.
 * This is a simplified interface that only handles state storage and retrieval.
 * Business logic is handled in the domain layer (ScoreCalculator and UseCases).
 */
interface ScoreRepository {
    /**
     * Get the current game state as a StateFlow.
     * StateFlow guarantees there's always a current value and new collectors
     * immediately receive the latest state.
     */
    fun getGameState(): StateFlow<GameState>

    /**
     * Update the game state with a new state.
     * This is the single write method - all state changes go through here.
     */
    suspend fun updateGameState(newState: GameState)
}
