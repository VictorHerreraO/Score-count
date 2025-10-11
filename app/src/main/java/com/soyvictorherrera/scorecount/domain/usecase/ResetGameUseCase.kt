package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.calculator.ScoreCalculator
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for resetting the game to initial state.
 * Orchestrates: fetch current state + settings → calculate reset state → save new state.
 */
class ResetGameUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(lastGameWinnerId: Int? = null) {
        val currentState = scoreRepository.getGameState().first()
        val settings = settingsRepository.getSettings().first()

        val newState = ScoreCalculator.resetGame(
            player1Id = currentState.player1.id,
            player2Id = currentState.player2.id,
            player1Name = currentState.player1.name,
            player2Name = currentState.player2.name,
            settings = settings,
            lastGameWinnerId = lastGameWinnerId
        )

        scoreRepository.updateGameState(newState)
    }
}
