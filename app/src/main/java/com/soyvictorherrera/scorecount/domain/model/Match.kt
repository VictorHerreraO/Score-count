package com.soyvictorherrera.scorecount.domain.model

data class Match(
    val id: String,
    val players: Pair<Player, Player>,
    val score: Pair<Int, Int>,
    val date: Long
)
