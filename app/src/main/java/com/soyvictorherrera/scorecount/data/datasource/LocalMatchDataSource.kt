package com.soyvictorherrera.scorecount.data.datasource

import com.soyvictorherrera.scorecount.data.database.dao.MatchDao
import com.soyvictorherrera.scorecount.data.mapper.MatchMapper
import com.soyvictorherrera.scorecount.domain.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MatchDataSource {
    fun getMatchList(): Flow<List<Match>>

    suspend fun saveMatch(match: Match)
}

class LocalMatchDataSource
    @Inject
    constructor(
        private val matchDao: MatchDao,
        private val matchMapper: MatchMapper
    ) : MatchDataSource {
        override fun getMatchList(): Flow<List<Match>> =
            matchDao.getMatchList().map {
                it.map(matchMapper::mapFromEntity)
            }

        override suspend fun saveMatch(match: Match) {
            matchDao.insert(matchMapper.mapToEntity(match))
        }
    }
