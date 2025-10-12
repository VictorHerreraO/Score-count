package com.soyvictorherrera.scorecount.data.datasource

import androidx.datastore.core.DataStore
import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.data.mapper.toDomain
import com.soyvictorherrera.scorecount.data.mapper.toProto
import com.soyvictorherrera.scorecount.di.ApplicationScope
import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlinx.coroutines.CoroutineScope
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
         * Update the game state and persist to disk.
         * This is the only write method - all state mutations go through here.
         */
        fun updateState(newState: GameState) {
            scope.launch {
                dataStore.updateData { newState.toProto() }
            }
        }
    }
