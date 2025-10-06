package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IncrementScoreUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(playerId: Int) {
        val gameState = scoreRepository.getGameState().first()
        val gameSettings = settingsRepository.getSettings().first()

        val player1Score = gameState.player1.score
        val player2Score = gameState.player2.score

        var isDeuce = gameState.isDeuce
        if (gameSettings.winByTwo &&
            !isDeuce &&
            player1Score == (gameSettings.pointsToWinSet - 1) &&
            player2Score == (gameSettings.pointsToWinSet - 1)
        ) {
            isDeuce = true
        }

        scoreRepository.incrementScore(playerId, isDeuce)
    }
}
