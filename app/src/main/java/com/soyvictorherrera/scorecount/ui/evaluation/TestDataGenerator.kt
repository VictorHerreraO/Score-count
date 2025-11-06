package com.soyvictorherrera.scorecount.ui.evaluation

import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.model.Point
import com.soyvictorherrera.scorecount.domain.model.Set
import com.soyvictorherrera.scorecount.domain.model.SetScore

object TestDataGenerator {
    // Player colors from spec
    private const val PLAYER1_COLOR = "#135BEC" // Blue
    private const val PLAYER2_COLOR = "#FB923C" // Orange

    val player1 = Player(id = 1, name = "Player 1", color = PLAYER1_COLOR)
    val player2 = Player(id = 2, name = "Player 2", color = PLAYER2_COLOR)

    /**
     * Generates a close game: 11-9 (20 points total)
     * Simulates back-and-forth scoring
     */
    fun generateCloseGame(): Set {
        val points =
            listOf(
                Point(1, 1, 1, 0),
                Point(2, 2, 1, 1),
                Point(3, 1, 2, 1),
                Point(4, 2, 2, 2),
                Point(5, 1, 3, 2),
                Point(6, 1, 4, 2),
                Point(7, 2, 4, 3),
                Point(8, 1, 5, 3),
                Point(9, 2, 5, 4),
                Point(10, 1, 6, 4),
                Point(11, 2, 6, 5),
                Point(12, 1, 7, 5),
                Point(13, 2, 7, 6),
                Point(14, 1, 8, 6),
                Point(15, 2, 8, 7),
                Point(16, 1, 9, 7),
                Point(17, 2, 9, 8),
                Point(18, 1, 10, 8),
                Point(19, 2, 10, 9),
                Point(20, 1, 11, 9)
            )
        return Set(
            setNumber = 1,
            points = points,
            finalScore = SetScore(11, 9),
            winnerId = 1
        )
    }

    /**
     * Generates a deuce game: 15-13 (28 points total)
     * Tests handling of extended games
     */
    fun generateDeuceGame(): Set {
        val points = mutableListOf<Point>()
        var p1Score = 0
        var p2Score = 0

        // First 10 points - even match
        repeat(10) { i ->
            if (i % 2 == 0) {
                p1Score++
            } else {
                p2Score++
            }
            points.add(Point(i + 1, if (i % 2 == 0) 1 else 2, p1Score, p2Score))
        }

        // 10-10, now deuce situation
        // Add 8 more points to reach 15-13
        val deucePattern = listOf(1, 2, 1, 2, 1, 1, 2, 1) // Player IDs
        deucePattern.forEachIndexed { index, playerId ->
            if (playerId == 1) p1Score++ else p2Score++
            points.add(Point(11 + index, playerId, p1Score, p2Score))
        }

        return Set(
            setNumber = 2,
            points = points,
            finalScore = SetScore(15, 13),
            winnerId = 1
        )
    }

    /**
     * Generates a landslide: 11-0 (11 points total)
     * Tests handling of one-sided games
     */
    fun generateLandslideGame(): Set {
        val points =
            (1..11).map { sequence ->
                Point(sequence, 1, sequence, 0)
            }
        return Set(
            setNumber = 3,
            points = points,
            finalScore = SetScore(11, 0),
            winnerId = 1
        )
    }

    /**
     * Generates empty set for error handling tests
     */
    fun generateEmptySet(): Set =
        Set(
            setNumber = 4,
            points = emptyList(),
            finalScore = SetScore(0, 0),
            winnerId = 0
        )
}
