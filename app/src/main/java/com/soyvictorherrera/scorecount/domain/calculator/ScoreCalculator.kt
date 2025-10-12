package com.soyvictorherrera.scorecount.domain.calculator

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlin.math.abs

/**
 * Pure business logic for score calculation.
 * Contains all game rules for table tennis scoring, server rotation, and match progression.
 * This class has no dependencies and is fully testable.
 */
object ScoreCalculator {
    /**
     * Calculate the new game state after a player scores a point.
     *
     * @param currentState The current game state
     * @param settings The game settings (rules)
     * @param playerId The ID of the player who scored
     * @return The new game state after the point is scored
     */
    fun incrementScore(
        currentState: GameState,
        settings: GameSettings,
        playerId: Int
    ): GameState {
        // Cannot score if game is already finished
        if (currentState.isFinished) return currentState

        // Increment the score for the player who scored
        var newP1Score = currentState.player1.score
        var newP2Score = currentState.player2.score

        when (playerId) {
            currentState.player1.id -> newP1Score++
            currentState.player2.id -> newP2Score++
            else -> return currentState // Invalid player ID
        }

        // Check if a player won the current set
        val p1CanWinSet = newP1Score >= settings.pointsToWinSet
        val p2CanWinSet = newP2Score >= settings.pointsToWinSet
        val scoreDiff = abs(newP1Score - newP2Score)

        val p1WinsSet = p1CanWinSet && (!settings.winByTwo || scoreDiff >= 2)
        val p2WinsSet = p2CanWinSet && (!settings.winByTwo || scoreDiff >= 2)

        var newP1Sets = currentState.player1SetsWon
        var newP2Sets = currentState.player2SetsWon
        var setJustEnded = false
        var lastSetWinnerId: Int? = null

        // Determine deuce state
        val isDeuce = calculateDeuceState(newP1Score, newP2Score, settings)

        // Handle set completion
        if (p1WinsSet || p2WinsSet) {
            if (p1WinsSet) {
                newP1Sets++
                lastSetWinnerId = currentState.player1.id
            } else {
                newP2Sets++
                lastSetWinnerId = currentState.player2.id
            }
            setJustEnded = true
        }

        // Reset scores if set ended
        if (setJustEnded) {
            newP1Score = 0
            newP2Score = 0
        }

        // Check if match is finished
        val setsToWinMatch = (settings.numberOfSets / 2) + 1
        val gameIsFinished = newP1Sets >= setsToWinMatch || newP2Sets >= setsToWinMatch

        // Determine next server
        val newServingPlayerId =
            determineNextServer(
                currentScores = Pair(newP1Score, newP2Score),
                currentServingPlayerId = currentState.servingPlayerId ?: currentState.player1.id,
                playerIds = Pair(currentState.player1.id, currentState.player2.id),
                settings = settings,
                setEnded = setJustEnded,
                lastSetWinnerId = lastSetWinnerId
            )

        return currentState.copy(
            player1 = currentState.player1.copy(score = newP1Score),
            player2 = currentState.player2.copy(score = newP2Score),
            player1SetsWon = newP1Sets,
            player2SetsWon = newP2Sets,
            servingPlayerId = newServingPlayerId,
            isDeuce = isDeuce,
            isFinished = gameIsFinished
        )
    }

    /**
     * Calculate the new game state after decrementing a player's score.
     * Note: This is a simplified implementation that only decrements within the current set.
     *
     * @param currentState The current game state
     * @param playerId The ID of the player whose score should be decremented
     * @return The new game state after the score is decremented
     */
    fun decrementScore(
        currentState: GameState,
        playerId: Int
    ): GameState {
        // Cannot decrement if game is finished
        if (currentState.isFinished) return currentState

        val newPlayer1 =
            if (playerId == currentState.player1.id && currentState.player1.score > 0) {
                currentState.player1.copy(score = currentState.player1.score - 1)
            } else {
                currentState.player1
            }

        val newPlayer2 =
            if (playerId == currentState.player2.id && currentState.player2.score > 0) {
                currentState.player2.copy(score = currentState.player2.score - 1)
            } else {
                currentState.player2
            }

        return currentState.copy(
            player1 = newPlayer1,
            player2 = newPlayer2
        )
    }

    /**
     * Switch the serving player manually.
     *
     * @param currentState The current game state
     * @return The new game state with the server switched
     */
    fun switchServe(currentState: GameState): GameState {
        // Cannot switch if game is finished
        if (currentState.isFinished) return currentState

        val nextServer =
            if (currentState.servingPlayerId == currentState.player1.id) {
                currentState.player2.id
            } else {
                currentState.player1.id
            }

        return currentState.copy(servingPlayerId = nextServer)
    }

    /**
     * Reset the game to initial state.
     *
     * @param player1Id The ID of player 1
     * @param player2Id The ID of player 2
     * @param player1Name The name of player 1
     * @param player2Name The name of player 2
     * @param settings The game settings
     * @param lastGameWinnerId Optional: ID of the last game winner (determines first server)
     * @return A new game state in initial configuration
     */
    fun resetGame(
        player1Id: Int,
        player2Id: Int,
        player1Name: String,
        player2Name: String,
        settings: GameSettings,
        lastGameWinnerId: Int? = null
    ): GameState {
        val firstServer =
            if (settings.winnerServesNextGame && lastGameWinnerId != null) {
                lastGameWinnerId
            } else {
                player1Id
            }

        return GameState(
            player1 =
                com.soyvictorherrera.scorecount.domain.model.Player(
                    id = player1Id,
                    name = player1Name,
                    score = 0
                ),
            player2 =
                com.soyvictorherrera.scorecount.domain.model.Player(
                    id = player2Id,
                    name = player2Name,
                    score = 0
                ),
            servingPlayerId = firstServer,
            player1SetsWon = 0,
            player2SetsWon = 0,
            isDeuce = false,
            isFinished = false
        )
    }

    /**
     * Determine the winner of the match based on sets won.
     *
     * @param gameState The current game state
     * @return The ID of the winning player, or null if there's a tie or no clear winner
     */
    fun determineWinner(gameState: GameState): Int? =
        when {
            gameState.player1SetsWon > gameState.player2SetsWon -> gameState.player1.id
            gameState.player2SetsWon > gameState.player1SetsWon -> gameState.player2.id
            else -> null // Tie or no clear winner
        }

    /**
     * Determine if the current scores represent a deuce situation.
     *
     * @param p1Score Player 1's score
     * @param p2Score Player 2's score
     * @param settings The game settings
     * @return True if the game is in deuce state
     */
    private fun calculateDeuceState(
        p1Score: Int,
        p2Score: Int,
        settings: GameSettings
    ): Boolean {
        if (!settings.winByTwo) return false

        return p1Score >= settings.pointsToWinSet - 1 &&
            p2Score >= settings.pointsToWinSet - 1
    }

    /**
     * Determine who should serve the next point based on game rules.
     *
     * @param currentScores Current scores (P1, P2) for the point about to be played
     * @param currentServingPlayerId The current server's ID
     * @param playerIds Pair of player IDs (P1, P2)
     * @param settings The game settings
     * @param setEnded Whether the set just ended
     * @param lastSetWinnerId The ID of the player who won the last set (if applicable)
     * @return The ID of the player who should serve next
     */
    private fun determineNextServer(
        currentScores: Pair<Int, Int>,
        currentServingPlayerId: Int,
        playerIds: Pair<Int, Int>,
        settings: GameSettings,
        setEnded: Boolean,
        lastSetWinnerId: Int?
    ): Int {
        val (p1Id, p2Id) = playerIds

        // If a set just ended, determine server for the new set
        if (setEnded) {
            return if (settings.winnerServesNextGame && lastSetWinnerId != null) {
                lastSetWinnerId
            } else {
                // Alternate server from who served last in the previous set
                if (currentServingPlayerId == p1Id) p2Id else p1Id
            }
        }

        // Determine if we're in deuce (at or above pointsToWinSet - 1)
        val atDeuce =
            currentScores.first >= settings.pointsToWinSet - 1 &&
                currentScores.second >= settings.pointsToWinSet - 1

        // Determine serve rotation interval
        val serveInterval =
            if (atDeuce && settings.serveChangeAfterDeuce > 0) {
                settings.serveChangeAfterDeuce
            } else {
                settings.serveRotationAfterPoints
            }

        // Check if it's time to rotate server
        // Note: currentScores represents the state AFTER scoring the point
        // The first N points are served by the initial server, then we rotate every N points
        // Example with N=2: P1 serves points 1-2, P2 serves points 3-4, P1 serves points 5-6, etc.
        // Rotation happens at the start of point N+1, 2N+1, 3N+1, etc.
        val totalPointsInCurrentSet = currentScores.first + currentScores.second

        // Rotate when: we've completed at least one interval AND the previous point was the last of an interval
        // (totalPoints - 1) % interval == 0 means the previous point was the Nth, 2Nth, 3Nth, etc.
        val shouldRotate =
            serveInterval > 0 &&
                totalPointsInCurrentSet > serveInterval &&
                (totalPointsInCurrentSet - serveInterval - 1) % serveInterval == 0

        return if (shouldRotate) {
            if (currentServingPlayerId == p1Id) p2Id else p1Id
        } else {
            currentServingPlayerId
        }
    }
}
