package com.soyvictorherrera.scorecount.domain.calculator

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import kotlin.math.abs

/**
 * Parameters for determining the next server.
 * Groups related data to reduce parameter list complexity.
 */
private data class ServerDeterminationParams(
    val currentScores: Pair<Int, Int>,
    val currentServingPlayerId: Int,
    val playerIds: Pair<Int, Int>,
    val settings: GameSettings,
    val setEnded: Boolean,
    val lastSetWinnerId: Int?
)

/**
 * Result of set completion processing.
 */
private data class SetCompletionResult(
    val newP1Sets: Int,
    val newP2Sets: Int,
    val setEnded: Boolean,
    val lastSetWinnerId: Int?,
    val newCompletedSets: List<com.soyvictorherrera.scorecount.domain.model.Set>,
    val newCurrentSetPoints: List<com.soyvictorherrera.scorecount.domain.model.Point>,
    val newSetNumber: Int
)

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

        // Update scores
        val (newP1Score, newP2Score) =
            updateScores(currentState, playerId)
                ?: return currentState // Invalid player ID

        // Create and add point
        val updatedPoints = addPointToSet(currentState, playerId, newP1Score, newP2Score)

        // Check for set completion
        val setResult =
            processSetCompletion(
                currentState,
                settings,
                newP1Score,
                newP2Score,
                updatedPoints
            )

        // Determine final scores and deuce state after potential set reset
        val (finalP1Score, finalP2Score, isDeuce) =
            calculateFinalScores(
                newP1Score,
                newP2Score,
                setResult.setEnded,
                settings
            )

        // Check if match is finished
        val gameIsFinished = isMatchFinished(setResult.newP1Sets, setResult.newP2Sets, settings)

        // Determine next server
        val newServingPlayerId =
            determineNextServer(
                ServerDeterminationParams(
                    currentScores = Pair(finalP1Score, finalP2Score),
                    currentServingPlayerId = currentState.servingPlayerId ?: currentState.player1.id,
                    playerIds = Pair(currentState.player1.id, currentState.player2.id),
                    settings = settings,
                    setEnded = setResult.setEnded,
                    lastSetWinnerId = setResult.lastSetWinnerId
                )
            )

        return currentState.copy(
            player1 = currentState.player1.copy(score = finalP1Score),
            player2 = currentState.player2.copy(score = finalP2Score),
            player1SetsWon = setResult.newP1Sets,
            player2SetsWon = setResult.newP2Sets,
            servingPlayerId = newServingPlayerId,
            isDeuce = isDeuce,
            isFinished = gameIsFinished,
            currentSetPoints = setResult.newCurrentSetPoints,
            completedSets = setResult.newCompletedSets,
            currentSetNumber = setResult.newSetNumber
        )
    }

    /**
     * Calculate the new game state after decrementing a player's score.
     * Note: This is a simplified implementation that only decrements within the current set.
     *
     * @param currentState The current game state
     * @param playerId The ID of the player whose score should be decremented
     * @param settings The game settings (needed to recalculate deuce state)
     * @return The new game state after the score is decremented
     */
    fun decrementScore(
        currentState: GameState,
        playerId: Int,
        settings: GameSettings
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

        // Recalculate deuce state based on new scores
        val isDeuce = calculateDeuceState(newPlayer1.score, newPlayer2.score, settings)

        return currentState.copy(
            player1 = newPlayer1,
            player2 = newPlayer2,
            isDeuce = isDeuce
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
     * @param lastGameWinnerId Optional: ID of the last game winner
     *   (determines first server when winnerServesNextGame is true)
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
                // Winner serves next game
                lastGameWinnerId
            } else {
                // Always reset to player 1 (initial state)
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
            isFinished = false,
            currentSetPoints = emptyList(),
            completedSets = emptyList(),
            currentSetNumber = 1
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
     * @param params Parameters for server determination
     * @return The ID of the player who should serve next
     */
    private fun determineNextServer(params: ServerDeterminationParams): Int {
        val (currentScores, currentServingPlayerId, playerIds, settings, setEnded, lastSetWinnerId) = params
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
        // servingPlayerId indicates who will serve the NEXT point
        // Example with interval=2: P1 serves points 1-2, P2 serves points 3-4, P1 serves points 5-6
        // After point 2 is scored (total=2), we rotate so P2 will serve point 3
        val totalPointsInCurrentSet = currentScores.first + currentScores.second

        if (serveInterval <= 0) {
            return currentServingPlayerId
        }

        // Rotate when we've completed a serve interval (total points is a multiple of interval)
        // This ensures the server changes for the next point to be played
        val shouldRotate =
            totalPointsInCurrentSet > 0 &&
                totalPointsInCurrentSet % serveInterval == 0

        return if (shouldRotate) {
            if (currentServingPlayerId == p1Id) p2Id else p1Id
        } else {
            currentServingPlayerId
        }
    }

    /**
     * Update scores after a player scores a point.
     * Returns null if playerId is invalid.
     */
    private fun updateScores(
        currentState: GameState,
        playerId: Int
    ): Pair<Int, Int>? {
        var newP1Score = currentState.player1.score
        var newP2Score = currentState.player2.score

        when (playerId) {
            currentState.player1.id -> newP1Score++
            currentState.player2.id -> newP2Score++
            else -> return null
        }

        return Pair(newP1Score, newP2Score)
    }

    /**
     * Create a Point object and add it to the current set's points.
     */
    private fun addPointToSet(
        currentState: GameState,
        playerId: Int,
        newP1Score: Int,
        newP2Score: Int
    ): List<com.soyvictorherrera.scorecount.domain.model.Point> {
        val newPoint =
            com.soyvictorherrera.scorecount.domain.model.Point(
                sequence = currentState.currentSetPoints.size + 1,
                scorerId = playerId,
                player1Score = newP1Score,
                player2Score = newP2Score
            )
        return currentState.currentSetPoints + newPoint
    }

    /**
     * Process set completion logic including checking if a set is won
     * and creating the completed set object.
     */
    private fun processSetCompletion(
        currentState: GameState,
        settings: GameSettings,
        newP1Score: Int,
        newP2Score: Int,
        updatedPoints: List<com.soyvictorherrera.scorecount.domain.model.Point>
    ): SetCompletionResult {
        val p1CanWinSet = newP1Score >= settings.pointsToWinSet
        val p2CanWinSet = newP2Score >= settings.pointsToWinSet
        val scoreDiff = abs(newP1Score - newP2Score)

        val p1WinsSet = p1CanWinSet && (!settings.winByTwo || scoreDiff >= 2)
        val p2WinsSet = p2CanWinSet && (!settings.winByTwo || scoreDiff >= 2)

        var newP1Sets = currentState.player1SetsWon
        var newP2Sets = currentState.player2SetsWon
        var setJustEnded = false
        var lastSetWinnerId: Int? = null

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

        val newCompletedSets: List<com.soyvictorherrera.scorecount.domain.model.Set>
        val newCurrentSetPoints: List<com.soyvictorherrera.scorecount.domain.model.Point>
        val newSetNumber: Int

        if (setJustEnded) {
            val completedSet =
                com.soyvictorherrera.scorecount.domain.model.Set(
                    setNumber = currentState.currentSetNumber,
                    points = updatedPoints,
                    finalScore =
                        com.soyvictorherrera.scorecount.domain.model.SetScore(
                            player1Score = newP1Score,
                            player2Score = newP2Score
                        ),
                    winnerId = lastSetWinnerId!!
                )
            newCompletedSets = currentState.completedSets + completedSet
            newCurrentSetPoints = emptyList()
            newSetNumber = currentState.currentSetNumber + 1
        } else {
            newCompletedSets = currentState.completedSets
            newCurrentSetPoints = updatedPoints
            newSetNumber = currentState.currentSetNumber
        }

        return SetCompletionResult(
            newP1Sets = newP1Sets,
            newP2Sets = newP2Sets,
            setEnded = setJustEnded,
            lastSetWinnerId = lastSetWinnerId,
            newCompletedSets = newCompletedSets,
            newCurrentSetPoints = newCurrentSetPoints,
            newSetNumber = newSetNumber
        )
    }

    /**
     * Calculate final scores and deuce state after potential set reset.
     * Returns Triple(p1Score, p2Score, isDeuce).
     */
    private fun calculateFinalScores(
        p1Score: Int,
        p2Score: Int,
        setEnded: Boolean,
        settings: GameSettings
    ): Triple<Int, Int, Boolean> =
        if (setEnded) {
            Triple(0, 0, false)
        } else {
            Triple(p1Score, p2Score, calculateDeuceState(p1Score, p2Score, settings))
        }

    /**
     * Check if the match is finished based on sets won.
     */
    private fun isMatchFinished(
        p1Sets: Int,
        p2Sets: Int,
        settings: GameSettings
    ): Boolean {
        val setsToWinMatch = (settings.numberOfSets / 2) + 1
        return p1Sets >= setsToWinMatch || p2Sets >= setsToWinMatch
    }
}
