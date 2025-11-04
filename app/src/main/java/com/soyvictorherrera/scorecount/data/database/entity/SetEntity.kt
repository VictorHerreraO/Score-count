package com.soyvictorherrera.scorecount.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["match_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["match_id"])]
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "match_id")
    val matchId: Long,
    @ColumnInfo(name = "set_number")
    val setNumber: Int,
    @ColumnInfo(name = "final_score_p1")
    val finalScoreP1: Int,
    @ColumnInfo(name = "final_score_p2")
    val finalScoreP2: Int,
    @ColumnInfo(name = "winner_id")
    val winnerId: Int
)
