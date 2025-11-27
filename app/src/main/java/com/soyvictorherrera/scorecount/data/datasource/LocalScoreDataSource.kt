package com.soyvictorherrera.scorecount.data.datasource

import androidx.datastore.core.DataStore
import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.data.mapper.toDomain
import com.soyvictorherrera.scorecount.data.mapper.toProto
import com.soyvictorherrera.scorecount.di.ApplicationScope
import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for game state.
 * Persists GameState to disk using Proto DataStore and exposes it as a StateFlow.
 * This component contains NO business logic - all game rules are in the domain layer (ScoreCalculator).
 */
@Singleton
class LocalScoreDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<GameStateProto>,
        @ApplicationScope private val scope: CoroutineScope
    ) {
        /**
         * In-memory history stack for undo functionality.
         * Limited to 50 states to prevent memory issues.
         * Session-only (not persisted).
         */
        private val history = mutableListOf<GameState>()
        private val _hasUndoHistory = MutableStateFlow(false)

        /**
         * GameState loaded from disk and exposed as StateFlow.
         * Automatically restored on app restart.
         */
        val gameState: StateFlow<GameState> =
            dataStore.data
                .map { proto -> proto.toDomain() }
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Eagerly,
                    initialValue = GameStateSerializer.defaultValue.toDomain()
                )

        /**
         * Whether undo is available (history is not empty).
         */
        val hasUndoHistory: StateFlow<Boolean> = _hasUndoHistory

        /**
         * Update the game state and persist to disk.
         * This is the only write method - all state mutations go through here.
         * Pushes current state to history before updating.
         */
        fun updateState(newState: GameState) {
            scope.launch {
                // Push current state to history before updating
                history.add(gameState.value)

                // Limit history size to prevent memory issues
                if (history.size > MAX_HISTORY_SIZE) {
                    history.removeAt(0) // Remove oldest
                }

                // Update state
                dataStore.updateData { newState.toProto() }

                // Update undo availability
                _hasUndoHistory.value = history.isNotEmpty()
            }
        }

        /**
         * Undo the last state change and restore the previous state.
         * Has no effect if there is no history.
         */
        fun undoLastChange() {
            scope.launch {
                if (history.isEmpty()) return@launch

                // Pop last state from history
                val previousState = history.removeAt(history.lastIndex)

                // Restore without adding to history
                dataStore.updateData { previousState.toProto() }

                // Update undo availability
                _hasUndoHistory.value = history.isNotEmpty()
            }
        }

        /**
         * Clear the undo history.
         * Called when starting a new game or resetting.
         */
        fun clearHistory() {
            history.clear()
            _hasUndoHistory.value = false
        }

        companion object {
            private const val MAX_HISTORY_SIZE = 50
        }
    }
