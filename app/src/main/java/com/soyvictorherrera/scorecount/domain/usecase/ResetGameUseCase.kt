package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject

class ResetGameUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    suspend fun execute() {
        scoreRepository.resetGame()
    }
}
