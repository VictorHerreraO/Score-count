package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.calculator.ScoreCalculator
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for decrementing a player's score.
 * Orchestrates: fetch current state → calculate new state → save new state.
 */
class DecrementScoreUseCase
    @Inject
    constructor(
        private val scoreRepository: ScoreRepository,
        private val settingsRepository: SettingsRepository
    ) {
        suspend operator fun invoke(playerId: Int) {
            val currentState = scoreRepository.getGameState().first()
            val settings = settingsRepository.getSettings().first()
            val newState = ScoreCalculator.decrementScore(currentState, playerId, settings)
            scoreRepository.updateGameState(newState)
        }
    }
