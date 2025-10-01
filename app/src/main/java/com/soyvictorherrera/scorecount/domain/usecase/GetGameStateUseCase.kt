package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGameStateUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    fun execute(): Flow<GameState> {
        return scoreRepository.getGameState()
    }
}
