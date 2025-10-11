package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatchesUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    operator fun invoke(): Flow<List<Match>> {
        return matchRepository.getMatchList()
    }
}
