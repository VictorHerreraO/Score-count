package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.LocalScoreDataSource
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ScoreRepositoryImpl @Inject constructor(private val localDataSource: LocalScoreDataSource) : ScoreRepository {

    override fun getGameState(): Flow<GameState> = localDataSource.gameState

    override suspend fun incrementScore(playerId: Int) {
        localDataSource.incrementScore(playerId)
    }

    override suspend fun decrementScore(playerId: Int) {
        localDataSource.decrementScore(playerId)
    }

    override suspend fun manualSwitchServe() { // Renamed from switchServe()
        localDataSource.manualSwitchServe()
    }

    override suspend fun resetGame() {
        localDataSource.resetGame()
    }

    override suspend fun undoLastAction() {
        localDataSource.undoLastAction()
    }
}
