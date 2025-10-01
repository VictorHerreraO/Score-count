package com.soyvictorherrera.scorecount.domain.model

data class Player(
    val id: Int,
    val name: String,
    var score: Int = 0
)
