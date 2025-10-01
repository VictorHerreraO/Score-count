package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject

class IncrementScoreUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    suspend fun execute(playerId: Int) {
        scoreRepository.incrementScore(playerId)
    }
}
