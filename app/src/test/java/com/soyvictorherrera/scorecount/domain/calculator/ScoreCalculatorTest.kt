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

        defaultSettings = GameSettings(
            pointsToWinSet = 11,
            winByTwo = true,
            numberOfSets = 5,
            serveRotationAfterPoints = 2,
            serveChangeAfterDeuce = 1,
            winnerServesNextGame = true
        )

        initialState = GameState(
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
        val state = initialState.copy(
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
        val state = initialState.copy(
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
        val state = initialState.copy(
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
        val state = initialState.copy(
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

        // Point 1: P1 serves
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 2: P1 still serves
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 3: P2 now serves (after 2 points)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 4: P2 still serves
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 5: P1 serves again
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)
    }

    @Test
    fun `server rotates every point in deuce`() {
        val state = initialState.copy(
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
        val state = initialState.copy(
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
        val state = initialState.copy(
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
        val state = initialState.copy(
            player1 = player1.copy(score = 5),
            player2 = player2.copy(score = 3)
        )

        val newState = ScoreCalculator.decrementScore(state, player1.id)

        assertEquals(4, newState.player1.score)
        assertEquals(3, newState.player2.score)
    }

    @Test
    fun `decrementScore does not go below zero`() {
        val state = initialState.copy(
            player1 = player1.copy(score = 0),
            player2 = player2.copy(score = 3)
        )

        val newState = ScoreCalculator.decrementScore(state, player1.id)

        assertEquals(0, newState.player1.score)
    }

    @Test
    fun `decrementScore does not change state if game is finished`() {
        val finishedState = initialState.copy(
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
        val finishedState = initialState.copy(
            isFinished = true,
            servingPlayerId = player1.id
        )

        val newState = ScoreCalculator.switchServe(finishedState)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `resetGame creates initial state with player 1 serving by default`() {
        val newState = ScoreCalculator.resetGame(
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
        val newState = ScoreCalculator.resetGame(
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

        val newState = ScoreCalculator.resetGame(
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
        val state = initialState.copy(
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

        // Score 5 points, should still be P1 serving
        repeat(5) {
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
        }
        assertEquals(player1.id, state.servingPlayerId)

        // On 6th point, should switch to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)
    }

    @Test
    fun `win by two disabled allows win at exact points to win`() {
        val settings = defaultSettings.copy(winByTwo = false)
        val state = initialState.copy(
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
        val state = initialState.copy(
            player1SetsWon = 3,
            player2SetsWon = 1
        )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(player1.id, winnerId)
    }

    @Test
    fun `determineWinner returns player 2 id when player 2 has more sets`() {
        val state = initialState.copy(
            player1SetsWon = 1,
            player2SetsWon = 3
        )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(player2.id, winnerId)
    }

    @Test
    fun `determineWinner returns null when sets are tied`() {
        val state = initialState.copy(
            player1SetsWon = 2,
            player2SetsWon = 2
        )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(null, winnerId)
    }

    @Test
    fun `determineWinner returns null when both players have zero sets`() {
        val state = initialState.copy(
            player1SetsWon = 0,
            player2SetsWon = 0
        )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(null, winnerId)
    }
}
