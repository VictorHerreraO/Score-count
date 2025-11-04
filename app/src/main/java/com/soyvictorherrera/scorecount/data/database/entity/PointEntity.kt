package com.soyvictorherrera.scorecount.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "points",
    foreignKeys = [
        ForeignKey(
            entity = SetEntity::class,
            parentColumns = ["id"],
            childColumns = ["set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["set_id"])]
)
data class PointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "set_id")
    val setId: Long,
    @ColumnInfo(name = "sequence")
    val sequence: Int,
    @ColumnInfo(name = "scorer_id")
    val scorerId: Int,
    @ColumnInfo(name = "player1_score")
    val player1Score: Int,
    @ColumnInfo(name = "player2_score")
    val player2Score: Int
)
