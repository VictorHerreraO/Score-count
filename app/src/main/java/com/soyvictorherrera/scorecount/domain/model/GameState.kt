package com.soyvictorherrera.scorecount.domain.model

data class GameState(
    val player1: Player,
    val player2: Player,
    val servingPlayerId: Int?,
    val player1SetsWon: Int = 0,
    val player2SetsWon: Int = 0,
    val isFinished: Boolean = false
)
