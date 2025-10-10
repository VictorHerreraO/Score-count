package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.LocalScoreDataSource
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of ScoreRepository.
 * Simple pass-through to LocalScoreDataSource with no business logic.
 */
class ScoreRepositoryImpl @Inject constructor(
    private val localDataSource: LocalScoreDataSource
) : ScoreRepository {

    override fun getGameState(): Flow<GameState> = localDataSource.gameState

    override suspend fun updateGameState(newState: GameState) {
        localDataSource.updateState(newState)
    }
}
