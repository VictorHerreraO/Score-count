package com.soyvictorherrera.scorecount.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Represents a complete match with all sets and points.
 * Used for detailed match queries with full history.
 */
data class MatchWithSets(
    @Embedded
    val match: MatchEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "match_id",
        entity = SetEntity::class
    )
    val sets: List<SetWithPoints>
)

/**
 * Represents a set with all its points.
 * Intermediate relationship for nested queries.
 */
data class SetWithPoints(
    @Embedded
    val set: SetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "set_id"
    )
    val points: List<PointEntity>
)
