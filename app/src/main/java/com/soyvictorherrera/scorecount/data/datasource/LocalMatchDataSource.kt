package com.soyvictorherrera.scorecount.data.datasource

import com.soyvictorherrera.scorecount.data.database.dao.MatchDao
import com.soyvictorherrera.scorecount.data.database.dao.PointDao
import com.soyvictorherrera.scorecount.data.database.dao.SetDao
import com.soyvictorherrera.scorecount.data.mapper.MatchMapper
import com.soyvictorherrera.scorecount.data.mapper.SetMapper
import com.soyvictorherrera.scorecount.domain.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MatchDataSource {
    fun getMatchList(): Flow<List<Match>>

    fun getMatchById(id: String): Flow<Match?>

    suspend fun saveMatch(match: Match)
}

class LocalMatchDataSource
    @Inject
    constructor(
        private val matchDao: MatchDao,
        private val setDao: SetDao,
        private val pointDao: PointDao,
        private val matchMapper: MatchMapper,
        private val setMapper: SetMapper
    ) : MatchDataSource {
        override fun getMatchList(): Flow<List<Match>> =
            matchDao.getMatchList().map {
                it.map(matchMapper::mapFromEntity)
            }

        override fun getMatchById(id: String): Flow<Match?> {
            val matchId = id.toLongOrNull() ?: return flowOf(null)
            return matchDao.getMatchById(matchId).map { matchWithSets ->
                matchWithSets?.let(matchMapper::mapFromEntity)
            }
        }

        override suspend fun saveMatch(match: Match) {
            // Save match entity
            val matchEntity = matchMapper.mapToEntity(match)
            matchDao.insert(matchEntity)

            val matchId = matchEntity.id

            // Save sets and points if present
            if (match.sets.isNotEmpty()) {
                match.sets.forEach { set ->
                    // Save set and get generated ID
                    val setEntity = setMapper.mapToEntity(set, matchId)
                    val setId = setDao.insertSet(setEntity)

                    // Save all points for this set
                    if (set.points.isNotEmpty()) {
                        val pointEntities = setMapper.mapPointsToEntities(set.points, setId)
                        pointDao.insertPoints(pointEntities)
                    }
                }
            }
        }
    }
