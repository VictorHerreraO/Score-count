package com.soyvictorherrera.scorecount.util.fakes

import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake implementation of [MatchRepository] for testing.
 *
 * This fake provides:
 * - An empty initial match list
 * - Append-only match saving behavior
 * - Observable match list via Flow
 */
class FakeMatchRepository : MatchRepository {
    private val _matches = MutableStateFlow<List<Match>>(emptyList())

    override fun getMatchList(): Flow<List<Match>> = _matches

    override suspend fun saveMatch(match: Match) {
        _matches.value = _matches.value + match
    }
}
