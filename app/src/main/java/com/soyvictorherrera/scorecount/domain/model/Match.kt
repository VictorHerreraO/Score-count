package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a complete match with detailed set and point tracking.
 *
 * Contains the full match history including all sets played,
 * point-by-point progression, and match outcome.
 *
 * @property id Unique identifier for the match
 * @property playerOneName Name of player 1
 * @property playerTwoName Name of player 2
 * @property playerOneScore Number of sets won by player 1
 * @property playerTwoScore Number of sets won by player 2
 * @property date Match date as Unix timestamp (milliseconds)
 * @property sets List of all sets played in the match (empty for legacy matches)
 * @property winnerId ID of the match winner (null for ties or legacy matches)
 */
data class Match(
    val id: String,
    val playerOneName: String,
    val playerTwoName: String,
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val date: Long,
    val sets: List<Set> = emptyList(),
    val winnerId: Int? = null
)
