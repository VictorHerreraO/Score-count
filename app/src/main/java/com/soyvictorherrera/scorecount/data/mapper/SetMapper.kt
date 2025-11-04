package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.data.database.entity.SetEntity
import com.soyvictorherrera.scorecount.domain.model.Point
import com.soyvictorherrera.scorecount.domain.model.Set
import com.soyvictorherrera.scorecount.domain.model.SetScore
import javax.inject.Inject

class SetMapper
    @Inject
    constructor(
        private val pointMapper: PointMapper
    ) {
        /**
         * Maps SetEntity with its points to domain Set model.
         */
        fun mapFromEntity(
            entity: SetEntity,
            points: List<PointEntity>
        ): Set =
            Set(
                setNumber = entity.setNumber,
                points = points.map(pointMapper::mapFromEntity),
                finalScore =
                    SetScore(
                        player1Score = entity.finalScoreP1,
                        player2Score = entity.finalScoreP2
                    ),
                winnerId = entity.winnerId
            )

        /**
         * Maps domain Set to SetEntity.
         * Requires matchId for foreign key relationship.
         */
        fun mapToEntity(
            domain: Set,
            matchId: Long
        ): SetEntity =
            SetEntity(
                matchId = matchId,
                setNumber = domain.setNumber,
                finalScoreP1 = domain.finalScore.player1Score,
                finalScoreP2 = domain.finalScore.player2Score,
                winnerId = domain.winnerId
            )

        /**
         * Maps domain Points to PointEntity list.
         * Helper method for converting set's points to entities.
         */
        fun mapPointsToEntities(
            points: List<Point>,
            setId: Long
        ): List<PointEntity> = points.map { pointMapper.mapToEntity(it, setId) }
    }
