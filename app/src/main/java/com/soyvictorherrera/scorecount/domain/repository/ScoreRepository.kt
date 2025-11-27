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

    /**
     * Undo the last state change and restore the previous state.
     * Has no effect if there is no history to undo.
     */
    suspend fun undoLastChange()

    /**
     * Get whether undo is available (history is not empty).
     * Returns a StateFlow that emits true when there are states in history.
     */
    fun hasUndoHistory(): StateFlow<Boolean>

    /**
     * Clear the undo history.
     * Called when starting a new game or resetting.
     */
    suspend fun clearHistory()
}
