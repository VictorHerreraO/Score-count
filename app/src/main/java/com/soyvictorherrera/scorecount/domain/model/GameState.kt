package com.soyvictorherrera.scorecount.domain.model

data class GameState(
    val player1: Player,
    val player2: Player,
    val servingPlayerId: Int?,
    val isFinished: Boolean = false
)
