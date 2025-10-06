package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getMatchList(): Flow<List<Match>>
    suspend fun saveMatch(match: Match)
}
