package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject

class SwitchServeUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    suspend fun execute() {
        scoreRepository.switchServe()
    }
}
