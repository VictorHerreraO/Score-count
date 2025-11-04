package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a single point in a set with cumulative score tracking.
 *
 * This model captures each point scored during a set, including:
 * - Sequential ordering of points
 * - Which player scored the point
 * - Cumulative scores for both players after this point
 *
 * Used for point-by-point analysis and score progression visualization.
 *
 * @property sequence The sequential number of this point (1, 2, 3... N)
 * @property scorerId The ID of the player who scored this point
 * @property player1Score Player 1's cumulative score after this point
 * @property player2Score Player 2's cumulative score after this point
 */
data class Point(
    val sequence: Int,
    val scorerId: Int,
    val player1Score: Int,
    val player2Score: Int
)
