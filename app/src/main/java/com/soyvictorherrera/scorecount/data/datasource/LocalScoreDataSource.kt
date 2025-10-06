package com.soyvictorherrera.scorecount.data.datasource

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

// For a more robust undo, we'd need to store a history of states
// For now, let's assume a very simple "last state" for undo.
private var previousGameState: GameState? = null

// Initial default state
private val initialPlayer1 = Player(id = 1, name = "Player 1", score = 0)
private val initialPlayer2 = Player(id = 2, name = "Player 2", score = 0)
private val initialGameState = GameState(
    player1 = initialPlayer1,
    player2 = initialPlayer2,
    servingPlayerId = initialPlayer1.id,
    player1SetsWon = 0,
    player2SetsWon = 0,
    isDeuce = false,
    isFinished = false
)

@Singleton // Assuming a single instance of this data source
class LocalScoreDataSource @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    private val _gameState = MutableStateFlow(initialGameState)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private fun updateState(newState: GameState) {
        previousGameState = _gameState.value
        _gameState.value = newState
    }

    suspend fun incrementScore(playerId: Int, isDeuce: Boolean) {
        val currentState = _gameState.value
        if (currentState.isFinished) return // Game is already finished

        val settings = settingsRepository.getSettings().first()

        var newP1Score = currentState.player1.score
        var newP2Score = currentState.player2.score
        var newP1Sets = currentState.player1SetsWon
        var newP2Sets = currentState.player2SetsWon
        var gameIsFinished = currentState.isFinished
        var newServingPlayerId = currentState.servingPlayerId
        var setJustEnded = false
        var lastSetWinnerId: Int? = null

        if (playerId == currentState.player1.id) {
            newP1Score++
        } else if (playerId == currentState.player2.id) {
            newP2Score++
        }

        val p1CanWinSet = newP1Score >= settings.pointsToWinSet
        val p2CanWinSet = newP2Score >= settings.pointsToWinSet
        val scoreDiff = abs(newP1Score - newP2Score)

        val p1WinsSet = p1CanWinSet && (!settings.winByTwo || scoreDiff >= 2)
        val p2WinsSet = p2CanWinSet && (!settings.winByTwo || scoreDiff >= 2)

        var deuce = isDeuce
        if (p1WinsSet || p2WinsSet) {
            deuce = false
            if (p1WinsSet) {
                newP1Sets++
                lastSetWinnerId = currentState.player1.id
            } else {
                newP2Sets++
                lastSetWinnerId = currentState.player2.id
            }
            setJustEnded = true
        }

        if (setJustEnded) {
            newP1Score = 0
            newP2Score = 0
            val setsToWinMatch = (settings.numberOfSets / 2) + 1
            if (newP1Sets >= setsToWinMatch || newP2Sets >= setsToWinMatch) {
                gameIsFinished = true
            }
        }

        // Determine server for the next point/set
        newServingPlayerId = determineNextServer(
            currentScores = Pair(newP1Score, newP2Score), // Scores for the *next* point if set didn't end
            currentServingPlayerId = currentState.servingPlayerId ?: initialPlayer1.id,
            playerIds = Pair(currentState.player1.id, currentState.player2.id),
            settings = settings,
            setEnded = setJustEnded,
            lastSetWinnerId = lastSetWinnerId
        )

        updateState(
            currentState.copy(
                player1 = currentState.player1.copy(score = newP1Score),
                player2 = currentState.player2.copy(score = newP2Score),
                player1SetsWon = newP1Sets,
                player2SetsWon = newP2Sets,
                servingPlayerId = newServingPlayerId,
                isDeuce = deuce,
                isFinished = gameIsFinished
            )
        )
    }

    private fun determineNextServer(
        currentScores: Pair<Int, Int>, // Current scores of P1, P2 for the point *about* to be played (or 0,0 if set just ended)
        currentServingPlayerId: Int,
        playerIds: Pair<Int, Int>, // P1 ID, P2 ID
        settings: GameSettings,
        setEnded: Boolean,
        lastSetWinnerId: Int?
    ): Int {
        val (p1Id, p2Id) = playerIds

        if (setEnded) {
            return if (settings.winnerServesNextGame && lastSetWinnerId != null) {
                lastSetWinnerId
            } else {
                // Alternate server from who served last in the previous set, or a fixed player if it's the very first serve of a set.
                // This part can be complex (e.g. who served first in the *previous* set).
                // For now, let's just alternate from the current server if winner doesn't serve.
                if (currentServingPlayerId == p1Id) p2Id else p1Id
            }
        }

        // Deuce logic: if scores are at or above pointsToWinSet - 1
        val atDeuce = currentScores.first >= settings.pointsToWinSet - 1 && currentScores.second >= settings.pointsToWinSet - 1
        val serveInterval = if (atDeuce && settings.serveChangeAfterDeuce > 0) {
            settings.serveChangeAfterDeuce
        } else {
            settings.serveRotationAfterPoints
        }

        val totalPointsInCurrentGame = currentScores.first + currentScores.second
        if (serveInterval > 0 && totalPointsInCurrentGame % serveInterval == 0 && totalPointsInCurrentGame > 0) {
            return if (currentServingPlayerId == p1Id) p2Id else p1Id
        }

        return currentServingPlayerId // No change
    }


    suspend fun decrementScore(playerId: Int) {
        val currentState = _gameState.value
        if (currentState.isFinished) return // Cannot decrement if game is finished

        // Note: Decrementing score across set boundaries is complex and not fully handled here.
        // This simple decrement assumes we are within the current set.
        val newPlayer1 = if (playerId == currentState.player1.id && currentState.player1.score > 0) {
            currentState.player1.copy(score = currentState.player1.score - 1)
        } else {
            currentState.player1
        }
        val newPlayer2 = if (playerId == currentState.player2.id && currentState.player2.score > 0) {
            currentState.player2.copy(score = currentState.player2.score - 1)
        } else {
            currentState.player2
        }
        // Server does not change on decrement for simplicity, could be revisited.
        updateState(currentState.copy(player1 = newPlayer1, player2 = newPlayer2))
    }

    // This is for the manual "Switch Serve" button
    suspend fun manualSwitchServe() {
        val currentState = _gameState.value
        if (currentState.isFinished) return

        // val settings = settingsRepository.getSettings().first() // Not strictly needed for basic manual switch
        val nextServer = if (currentState.servingPlayerId == currentState.player1.id) {
            currentState.player2.id
        } else {
            currentState.player1.id
        }
        updateState(currentState.copy(servingPlayerId = nextServer))
    }

    suspend fun resetGame() {
        // Player names are part of initialGameState.
        // showNames setting is handled at the UI layer.
        // servingPlayerId can also be reset to a default or based on a new setting.
        updateState(
            initialGameState.copy(
                 servingPlayerId = initialPlayer1.id // Or based on a future setting
            )
        )
    }

    fun undoLastAction() {
        previousGameState?.let {
            _gameState.value = it
            previousGameState = null
        }
    }
}
