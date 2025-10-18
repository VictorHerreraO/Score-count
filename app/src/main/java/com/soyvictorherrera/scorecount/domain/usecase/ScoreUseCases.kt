package com.soyvictorherrera.scorecount.domain.usecase

import javax.inject.Inject

/**
 * Container for score-related use cases.
 *
 * Groups related use cases to reduce constructor parameter lists in ViewModels
 * and improve organization of business logic dependencies.
 */
data class ScoreUseCases
    @Inject
    constructor(
        val increment: IncrementScoreUseCase,
        val decrement: DecrementScoreUseCase,
        val switchServe: ManualSwitchServeUseCase,
        val reset: ResetGameUseCase,
        val saveMatch: SaveMatchUseCase
    )
