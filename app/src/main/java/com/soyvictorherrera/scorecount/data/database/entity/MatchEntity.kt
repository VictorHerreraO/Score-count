package com.soyvictorherrera.scorecount.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "player_one_name") val playerOneName: String,
    @ColumnInfo(name = "player_two_name") val playerTwoName: String,
    @ColumnInfo(name = "player_one_score") val playerOneScore: Int,
    @ColumnInfo(name = "player_two_score") val playerTwoScore: Int,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "winner_id") val winnerId: Int? = null
)
