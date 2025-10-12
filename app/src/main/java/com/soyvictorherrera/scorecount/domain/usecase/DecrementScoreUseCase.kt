package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.calculator.ScoreCalculator
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for decrementing a player's score.
 * Orchestrates: fetch current state → calculate new state → save new state.
 */
class DecrementScoreUseCase
    @Inject
    constructor(
        private val scoreRepository: ScoreRepository
    ) {
        suspend operator fun invoke(playerId: Int) {
            val currentState = scoreRepository.getGameState().first()
            val newState = ScoreCalculator.decrementScore(currentState, playerId)
            scoreRepository.updateGameState(newState)
        }
    }
