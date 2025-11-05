package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.MatchDataSource
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchRepositoryImpl
    @Inject
    constructor(
        private val dataSource: MatchDataSource
    ) : MatchRepository {
        override fun getMatchList(): Flow<List<Match>> = dataSource.getMatchList()

        override fun getMatchById(id: String): Flow<Match?> = dataSource.getMatchById(id)

        override suspend fun saveMatch(match: Match) {
            dataSource.saveMatch(match)
        }
    }
