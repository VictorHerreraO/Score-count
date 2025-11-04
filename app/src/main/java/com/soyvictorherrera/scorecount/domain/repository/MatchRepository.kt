package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getMatchList(): Flow<List<Match>>

    /**
     * Get a complete match by ID with all sets and points.
     * Returns null if match doesn't exist.
     */
    fun getMatchById(id: String): Flow<Match?>

    suspend fun saveMatch(match: Match)
}
