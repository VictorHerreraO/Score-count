package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.LocalScoreDataSource
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Implementation of ScoreRepository.
 * Simple pass-through to LocalScoreDataSource with no business logic.
 */
class ScoreRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: LocalScoreDataSource
    ) : ScoreRepository {
        override fun getGameState(): StateFlow<GameState> = localDataSource.gameState

        override suspend fun updateGameState(newState: GameState) {
            localDataSource.updateState(newState)
        }

        override suspend fun undoLastChange() {
            localDataSource.undoLastChange()
        }

        override fun hasUndoHistory(): StateFlow<Boolean> = localDataSource.hasUndoHistory

        override suspend fun clearHistory() {
            localDataSource.clearHistory()
        }
    }
