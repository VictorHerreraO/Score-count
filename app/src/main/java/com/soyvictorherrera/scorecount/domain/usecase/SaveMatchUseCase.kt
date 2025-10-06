package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import javax.inject.Inject

class SaveMatchUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend fun execute(match: Match) {
        matchRepository.saveMatch(match)
    }
}
