package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents the final score of a set.
 *
 * Simple value object for displaying set outcomes.
 *
 * @property player1Score Player 1's final score in the set
 * @property player2Score Player 2's final score in the set
 */
data class SetScore(
    val player1Score: Int,
    val player2Score: Int
)
