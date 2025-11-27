package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import javax.inject.Inject

/**
 * Use case for undoing the last score change.
 * Orchestrates: fetch history → restore previous state.
 */
class UndoScoreUseCase
    @Inject
    constructor(
        private val scoreRepository: ScoreRepository
    ) {
        suspend operator fun invoke() {
            scoreRepository.undoLastChange()
        }
    }
