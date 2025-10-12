package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.calculator.ScoreCalculator
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for manually switching the serving player.
 * Orchestrates: fetch current state → calculate new state → save new state.
 */
class ManualSwitchServeUseCase
    @Inject
    constructor(
        private val scoreRepository: ScoreRepository
    ) {
        suspend operator fun invoke() {
            val currentState = scoreRepository.getGameState().first()
            val newState = ScoreCalculator.switchServe(currentState)
            scoreRepository.updateGameState(newState)
        }
    }
