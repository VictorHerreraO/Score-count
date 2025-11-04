package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.domain.model.Point
import javax.inject.Inject

class PointMapper
    @Inject
    constructor() {
        /**
         * Maps PointEntity to domain Point model.
         */
        fun mapFromEntity(entity: PointEntity): Point =
            Point(
                sequence = entity.sequence,
                scorerId = entity.scorerId,
                player1Score = entity.player1Score,
                player2Score = entity.player2Score
            )

        /**
         * Maps domain Point to PointEntity.
         * Requires setId for foreign key relationship.
         */
        fun mapToEntity(
            domain: Point,
            setId: Long
        ): PointEntity =
            PointEntity(
                setId = setId,
                sequence = domain.sequence,
                scorerId = domain.scorerId,
                player1Score = domain.player1Score,
                player2Score = domain.player2Score
            )
    }
