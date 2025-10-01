package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject

class ManualSwitchServeUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    suspend fun execute() {
        scoreRepository.manualSwitchServe() // Assuming ScoreRepository will have this method
    }
}
