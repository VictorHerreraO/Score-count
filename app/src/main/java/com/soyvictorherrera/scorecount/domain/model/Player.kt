package com.soyvictorherrera.scorecount.domain.model

data class Player(
    val id: Int,
    val name: String,
    val score: Int = 0
)
