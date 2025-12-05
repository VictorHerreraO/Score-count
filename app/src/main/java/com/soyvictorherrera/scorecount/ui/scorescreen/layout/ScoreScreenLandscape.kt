package com.soyvictorherrera.scorecount.ui.scorescreen.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreenCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.CentralControls
import com.soyvictorherrera.scorecount.ui.scorescreen.components.CentralControlsCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCard
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardState

@Composable
fun ScoreScreenLandscape(
    gameState: GameState,
    gameSettings: GameSettings,
    hasUndoHistory: Boolean,
    callbacks: ScoreScreenCallbacks
) {
    Scaffold { paddingValues ->
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            PlayerScoreCard(
                state =
                    PlayerScoreCardState(
                        playerName = gameState.player1.name,
                        score = gameState.player1.score,
                        isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player1.id,
                        isFinished = gameState.isFinished
                    ),
                showPlayerName = gameSettings.showNames,
                callbacks =
                    PlayerScoreCardCallbacks(
                        onIncrement = { callbacks.onIncrement(gameState.player1.id) },
                        onDecrement = { callbacks.onDecrement(gameState.player1.id) }
                    ),
                modifier = Modifier.weight(1f)
            )

            CentralControls(
                modifier = Modifier.padding(horizontal = 4.dp),
                gameState = gameState,
                gameSettings = gameSettings,
                hasUndoHistory = hasUndoHistory,
                callbacks =
                    CentralControlsCallbacks(
                        onReset = callbacks.onReset,
                        onSwitchServe = callbacks.onSwitchServe,
                        onStartNewGame = callbacks.onStartNewGame,
                        onNavigateToHistory = callbacks.onNavigateToHistory,
                        onNavigateToSettings = callbacks.onNavigateToSettings,
                        onUndo = callbacks.onUndo
                    )
            )

            PlayerScoreCard(
                state =
                    PlayerScoreCardState(
                        playerName = gameState.player2.name,
                        score = gameState.player2.score,
                        isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player2.id,
                        isFinished = gameState.isFinished
                    ),
                showPlayerName = gameSettings.showNames,
                callbacks =
                    PlayerScoreCardCallbacks(
                        onIncrement = { callbacks.onIncrement(gameState.player2.id) },
                        onDecrement = { callbacks.onDecrement(gameState.player2.id) }
                    ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
