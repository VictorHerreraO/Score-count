package com.soyvictorherrera.scorecount.domain.calculator

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScoreCalculatorTest {
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var defaultSettings: GameSettings
    private lateinit var initialState: GameState

    @BeforeEach
    fun setup() {
        player1 = Player(id = 1, name = "Player 1", score = 0)
        player2 = Player(id = 2, name = "Player 2", score = 0)

        defaultSettings =
            GameSettings(
                pointsToWinSet = 11,
                winByTwo = true,
                numberOfSets = 5,
                serveRotationAfterPoints = 2,
                serveChangeAfterDeuce = 1,
                winnerServesNextGame = true
            )

        initialState =
            GameState(
                player1 = player1,
                player2 = player2,
                servingPlayerId = player1.id,
                player1SetsWon = 0,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = false
            )
    }

    @Test
    fun `incrementScore increases player score`() {
        val newState = ScoreCalculator.incrementScore(initialState, defaultSettings, player1.id)

        assertEquals(1, newState.player1.score)
        assertEquals(0, newState.player2.score)
    }

    @Test
    fun `incrementScore does not change state if game is finished`() {
        val finishedState = initialState.copy(isFinished = true)
        val newState = ScoreCalculator.incrementScore(finishedState, defaultSettings, player1.id)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `incrementScore detects deuce at 10-10`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 9)
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player2.id)

        assertTrue(newState.isDeuce)
        assertEquals(10, newState.player1.score)
        assertEquals(10, newState.player2.score)
    }

    @Test
    fun `incrementScore wins set at 11-9 without deuce`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 9)
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)

        assertEquals(0, newState.player1.score) // Reset after set win
        assertEquals(0, newState.player2.score)
        assertEquals(1, newState.player1SetsWon)
        assertEquals(0, newState.player2SetsWon)
        assertFalse(newState.isFinished)
    }

    @Test
    fun `incrementScore requires win by 2 in deuce`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10),
                isDeuce = true
            )

        // Score 11-10, should not win yet
        val afterP1Scores = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(11, afterP1Scores.player1.score)
        assertEquals(10, afterP1Scores.player2.score)
        assertEquals(0, afterP1Scores.player1SetsWon) // No set won yet

        // Score 12-10, should win
        val afterP1ScoresAgain = ScoreCalculator.incrementScore(afterP1Scores, defaultSettings, player1.id)
        assertEquals(0, afterP1ScoresAgain.player1.score) // Reset after win
        assertEquals(0, afterP1ScoresAgain.player2.score)
        assertEquals(1, afterP1ScoresAgain.player1SetsWon)
    }

    @Test
    fun `incrementScore finishes match when player wins 3 sets in best of 5`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                player1SetsWon = 2,
                player2SetsWon = 1
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)

        assertEquals(3, newState.player1SetsWon)
        assertTrue(newState.isFinished)
    }

    @Test
    fun `server rotates every 2 points`() {
        var state = initialState

        // Point 1: P1 serves (total=1, 1%2!=0, no rotation, P1 serves)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 2: After 2 points scored, rotate (total=2, 2%2==0, rotate to P2)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 3: P2 serves (total=3, 3%2!=0, no rotation, P2 serves)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 4: After 4 points total, rotate (total=4, 4%2==0, rotate to P1)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 5: P1 serves (total=5, 5%2!=0, no rotation, P1 serves)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)
    }

    @Test
    fun `server rotates every point in deuce`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10),
                isDeuce = true,
                servingPlayerId = player1.id
            )

        // First point in deuce: P2 serves (rotation after every point)
        val afterFirst = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, afterFirst.servingPlayerId)

        // Second point in deuce: P1 serves
        val afterSecond = ScoreCalculator.incrementScore(afterFirst, defaultSettings, player2.id)
        assertEquals(player1.id, afterSecond.servingPlayerId)
    }

    @Test
    fun `winner serves next set when winnerServesNextGame is true`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                servingPlayerId = player1.id
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)

        // Player 1 won the set, should serve first in next set
        assertEquals(player1.id, newState.servingPlayerId)
    }

    @Test
    fun `loser serves next set when winnerServesNextGame is false`() {
        val settings = defaultSettings.copy(winnerServesNextGame = false)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                servingPlayerId = player1.id
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        // Player 1 won, but loser (P2) should serve next set
        assertEquals(player2.id, newState.servingPlayerId)
    }

    @Test
    fun `decrementScore reduces player score`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 5),
                player2 = player2.copy(score = 3)
            )

        val newState = ScoreCalculator.decrementScore(state, player1.id)

        assertEquals(4, newState.player1.score)
        assertEquals(3, newState.player2.score)
    }

    @Test
    fun `decrementScore does not go below zero`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 0),
                player2 = player2.copy(score = 3)
            )

        val newState = ScoreCalculator.decrementScore(state, player1.id)

        assertEquals(0, newState.player1.score)
    }

    @Test
    fun `decrementScore does not change state if game is finished`() {
        val finishedState =
            initialState.copy(
                isFinished = true,
                player1 = player1.copy(score = 5)
            )

        val newState = ScoreCalculator.decrementScore(finishedState, player1.id)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `switchServe alternates serving player`() {
        val state = initialState.copy(servingPlayerId = player1.id)

        val afterSwitch1 = ScoreCalculator.switchServe(state)
        assertEquals(player2.id, afterSwitch1.servingPlayerId)

        val afterSwitch2 = ScoreCalculator.switchServe(afterSwitch1)
        assertEquals(player1.id, afterSwitch2.servingPlayerId)
    }

    @Test
    fun `switchServe does not change state if game is finished`() {
        val finishedState =
            initialState.copy(
                isFinished = true,
                servingPlayerId = player1.id
            )

        val newState = ScoreCalculator.switchServe(finishedState)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `resetGame creates initial state with player 1 serving by default`() {
        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = defaultSettings
            )

        assertEquals(1, newState.player1.id)
        assertEquals("Alice", newState.player1.name)
        assertEquals(0, newState.player1.score)

        assertEquals(2, newState.player2.id)
        assertEquals("Bob", newState.player2.name)
        assertEquals(0, newState.player2.score)

        assertEquals(1, newState.servingPlayerId)
        assertEquals(0, newState.player1SetsWon)
        assertEquals(0, newState.player2SetsWon)
        assertFalse(newState.isDeuce)
        assertFalse(newState.isFinished)
    }

    @Test
    fun `resetGame respects lastGameWinnerId when winnerServesNextGame is true`() {
        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = defaultSettings,
                lastGameWinnerId = 2
            )

        assertEquals(2, newState.servingPlayerId)
    }

    @Test
    fun `resetGame ignores lastGameWinnerId when winnerServesNextGame is false`() {
        val settings = defaultSettings.copy(winnerServesNextGame = false)

        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = settings,
                lastGameWinnerId = 2
            )

        assertEquals(1, newState.servingPlayerId) // Defaults to player 1
    }

    @Test
    fun `best of 3 sets match finishes at 2 sets won`() {
        val settings = defaultSettings.copy(numberOfSets = 3)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player1SetsWon = 1,
                player2SetsWon = 0
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        assertEquals(2, newState.player1SetsWon)
        assertTrue(newState.isFinished)
    }

    @Test
    fun `custom serve rotation interval works correctly`() {
        val settings = defaultSettings.copy(serveRotationAfterPoints = 5)
        var state = initialState

        // Score 5 points, after 5th point should rotate to P2
        repeat(5) {
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
        }
        assertEquals(player2.id, state.servingPlayerId)

        // On 6th point, P2 still serves
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)
    }

    @Test
    fun `win by two disabled allows win at exact points to win`() {
        val settings = defaultSettings.copy(winByTwo = false)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10)
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        // Should win immediately at 11-10
        assertEquals(0, newState.player1.score) // Reset after win
        assertEquals(1, newState.player1SetsWon)
        assertFalse(newState.isDeuce) // No deuce when winByTwo is false
    }

    @Test
    fun `invalid player id does not change state`() {
        val newState = ScoreCalculator.incrementScore(initialState, defaultSettings, 999)

        assertEquals(initialState, newState)
    }

    @Test
    fun `determineWinner returns player 1 id when player 1 has more sets`() {
        val state =
            initialState.copy(
                player1SetsWon = 3,
                player2SetsWon = 1
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(player1.id, winnerId)
    }

    @Test
    fun `determineWinner returns player 2 id when player 2 has more sets`() {
        val state =
            initialState.copy(
                player1SetsWon = 1,
                player2SetsWon = 3
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(player2.id, winnerId)
    }

    @Test
    fun `determineWinner returns null when sets are tied`() {
        val state =
            initialState.copy(
                player1SetsWon = 2,
                player2SetsWon = 2
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(null, winnerId)
    }

    @Test
    fun `determineWinner returns null when both players have zero sets`() {
        val state =
            initialState.copy(
                player1SetsWon = 0,
                player2SetsWon = 0
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(null, winnerId)
    }

    // Bug #42: Serve rotation with non-standard "Set to" values
    @Test
    fun `serve rotation works correctly with Set to 7 and rotation after 2`() {
        val settings = defaultSettings.copy(pointsToWinSet = 7, serveRotationAfterPoints = 2)
        var state = initialState

        // Point 1: P1 serves (total=1, no rotation)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId, "Point 1: P1 should serve")

        // Point 2: Rotation (total=2, 2%2==0, rotate to P2)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 2: After 2 points, rotate to P2")

        // Point 3: P2 serves (total=3, no rotation)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 3: P2 should serve")

        // Point 4: Rotation (total=4, 4%2==0, rotate to P1)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId, "Point 4: After 4 points, rotate to P1")

        // Point 5: P1 serves (total=5, no rotation)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId, "Point 5: P1 should serve")

        // Point 6: Rotation (total=6, 6%2==0, rotate to P2, score is 6-0)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 6: After 6 points, rotate to P2")
        assertEquals(6, state.player1.score)

        // Point 7: P1 scores and wins set (7-0)
        // After set win, scores reset and winner serves next (winnerServesNextGame=true)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(0, state.player1.score, "Scores should reset after set win")
        assertEquals(0, state.player2.score)
        assertEquals(1, state.player1SetsWon, "P1 should have won 1 set")
        assertEquals(player1.id, state.servingPlayerId, "Winner serves next set")
    }

    @Test
    fun `serve rotation works correctly with Set to 21 and rotation after 5`() {
        val settings = defaultSettings.copy(pointsToWinSet = 21, serveRotationAfterPoints = 5)
        var state = initialState

        // Points 1-4: P1 serves
        repeat(4) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player1.id, state.servingPlayerId, "Point ${i + 1}: P1 should serve")
        }

        // Point 5: After 5 total points, rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 5: After 5 points, rotate to P2")

        // Points 6-9: P2 serves
        repeat(4) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player2.id, state.servingPlayerId, "Point ${i + 6}: P2 should serve")
        }

        // Point 10: After 10 total points, rotate to P1
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId, "Point 10: After 10 points, rotate to P1")
    }

    @Test
    fun `serve rotation works with Set to 7 alternating scores`() {
        val settings = defaultSettings.copy(pointsToWinSet = 7, serveRotationAfterPoints = 2)
        var state = initialState

        // Point 1: P1 serves (P1 scores) - 1-0
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 2: Rotate to P2 (P1 scores) - 2-0
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 3: P2 serves (P2 scores) - 2-1
        state = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 4: Rotate to P1 (P2 scores) - 2-2
        state = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 5: P1 serves (P1 scores) - 3-2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 6: Rotate to P2 (P2 scores) - 3-3
        state = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 7: P2 serves (P1 scores) - 4-3
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)
    }

    @Test
    fun `deuce transition maintains correct server with Set to 7`() {
        val settings = defaultSettings.copy(pointsToWinSet = 7, serveRotationAfterPoints = 2)

        // Set up state: 6-5, total 11 points played
        // Pattern: P1 served 1-2, P2 served 3-4, P1 served 5-6, P2 served 7-8, P1 served 9-10, P2 serves 11-12
        val state =
            initialState.copy(
                player1 = player1.copy(score = 6),
                player2 = player2.copy(score = 5),
                servingPlayerId = player2.id // P2 should be serving (points 11-12)
            )

        // P2 scores to make it 6-6 (total = 12, deuce)
        val atDeuce = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertTrue(atDeuce.isDeuce, "Should be in deuce at 6-6")

        // In deuce, serve should rotate every point
        // Since P2 just served point 12, next point (13) should be P1
        assertEquals(player1.id, atDeuce.servingPlayerId, "At deuce, should rotate to P1")

        // Point 14 in deuce: should rotate to P2
        val afterDeuce1 = ScoreCalculator.incrementScore(atDeuce, settings, player1.id) // 7-6
        assertEquals(player2.id, afterDeuce1.servingPlayerId, "Deuce point 2: should rotate to P2")
    }

    @Test
    fun `deuce transition at 10-10 with standard settings`() {
        // State at 10-9, total 19 points
        // Pattern: P1 (1-2), P2 (3-4), P1 (5-6), P2 (7-8), P1 (9-10), P2 (11-12),
        // P1 (13-14), P2 (15-16), P1 (17-18), P2 serves 19-20
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 9),
                servingPlayerId = player2.id // P2 serving points 19-20
            )

        // P2 scores to make it 10-10 (total = 20, enters deuce)
        val atDeuce = ScoreCalculator.incrementScore(state, defaultSettings, player2.id)
        assertTrue(atDeuce.isDeuce, "Should be in deuce at 10-10")

        // P2 just served point 20, in deuce serve rotates every point, so P1 should serve next
        assertEquals(player1.id, atDeuce.servingPlayerId, "At 10-10 deuce, should rotate to P1")

        // Next point: should rotate to P2
        val afterDeuce1 = ScoreCalculator.incrementScore(atDeuce, defaultSettings, player1.id) // 11-10
        assertEquals(player2.id, afterDeuce1.servingPlayerId, "Deuce continues: should rotate to P2")
    }

    @Test
    fun `serve rotation with interval of 3`() {
        val settings = defaultSettings.copy(serveRotationAfterPoints = 3)
        var state = initialState

        // Points 1-2: P1 serves
        repeat(2) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player1.id, state.servingPlayerId, "Point ${i + 1}: P1 should serve")
        }

        // Point 3: After 3 points, rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 3: Rotate to P2")

        // Points 4-5: P2 serves
        repeat(2) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player2.id, state.servingPlayerId, "Point ${i + 4}: P2 should serve")
        }

        // Point 6: After 6 points, rotate to P1
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId, "Point 6: Rotate to P1")

        // Points 7-8: P1 serves
        repeat(2) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player1.id, state.servingPlayerId, "Point ${i + 7}: P1 should serve")
        }

        // Point 9: After 9 points, rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 9: Rotate to P2")
    }
}
