package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.data.database.entity.MatchWithSets
import com.soyvictorherrera.scorecount.domain.model.Match
import javax.inject.Inject

class MatchMapper
    @Inject
    constructor(
        private val setMapper: SetMapper
    ) {
        /**
         * Maps basic MatchEntity to Match (without sets).
         * Used for match list queries.
         */
        fun mapFromEntity(entity: MatchEntity): Match =
            Match(
                id = entity.id.toString(),
                playerOneName = entity.playerOneName,
                playerTwoName = entity.playerTwoName,
                playerOneScore = entity.playerOneScore,
                playerTwoScore = entity.playerTwoScore,
                date = entity.date,
                sets = emptyList(),
                winnerId = entity.winnerId
            )

        /**
         * Maps complete MatchWithSets to Match (with all nested data).
         * Used for detailed match queries.
         */
        fun mapFromEntity(matchWithSets: MatchWithSets): Match =
            Match(
                id = matchWithSets.match.id.toString(),
                playerOneName = matchWithSets.match.playerOneName,
                playerTwoName = matchWithSets.match.playerTwoName,
                playerOneScore = matchWithSets.match.playerOneScore,
                playerTwoScore = matchWithSets.match.playerTwoScore,
                date = matchWithSets.match.date,
                sets =
                    matchWithSets.sets.map { setWithPoints ->
                        setMapper.mapFromEntity(setWithPoints.set, setWithPoints.points)
                    },
                winnerId = matchWithSets.match.winnerId
            )

        /**
         * Maps Match to MatchEntity (basic fields only).
         */
        fun mapToEntity(domain: Match): MatchEntity =
            MatchEntity(
                id = domain.id.toLongOrNull() ?: 0,
                playerOneName = domain.playerOneName,
                playerTwoName = domain.playerTwoName,
                playerOneScore = domain.playerOneScore,
                playerTwoScore = domain.playerTwoScore,
                date = domain.date,
                winnerId = domain.winnerId
            )
    }
