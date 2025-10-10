package com.soyvictorherrera.scorecount.domain.model

data class Match(
    val id: String,
    val playerOneName: String,
    val playerTwoName: String,
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val date: Long
)
