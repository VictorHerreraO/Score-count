package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a complete set in a match with point-by-point tracking.
 *
 * Contains the full history of points played during the set,
 * the final score, and the winner. Used for detailed match analysis
 * and score progression visualization.
 *
 * @property setNumber The set number in the match (1, 2, 3, etc.)
 * @property points List of all points played in sequential order
 * @property finalScore The final score of the set
 * @property winnerId The ID of the player who won this set
 */
data class Set(
    val setNumber: Int,
    val points: List<Point>,
    val finalScore: SetScore,
    val winnerId: Int
)
