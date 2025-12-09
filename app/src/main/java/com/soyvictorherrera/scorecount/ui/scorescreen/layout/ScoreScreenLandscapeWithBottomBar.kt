package com.soyvictorherrera.scorecount.ui.scorescreen.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreenCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.BottomBarActions
import com.soyvictorherrera.scorecount.ui.scorescreen.components.BottomBarActionsCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.DeuceIndicator
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCard
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardState

@Composable
fun ScoreScreenLandscapeWithBottomBar(
    gameState: GameState,
    gameSettings: GameSettings,
    hasUndoHistory: Boolean,
    callbacks: ScoreScreenCallbacks
) {
    val showDeuceIndicator = gameSettings.markDeuce && gameState.isDeuce

    Scaffold(
        bottomBar = {
            BottomBarActions(
                isFinished = gameState.isFinished,
                showSwitchServe = gameSettings.markServe,
                hasUndoHistory = hasUndoHistory,
                callbacks =
                    BottomBarActionsCallbacks(
                        onReset = callbacks.onReset,
                        onSwitchServe = callbacks.onSwitchServe,
                        onStartNewGame = callbacks.onStartNewGame,
                        onUndo = callbacks.onUndo,
                        onSettings = callbacks.onNavigateToSettings
                    )
            )
        }
    ) { paddingValues ->
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
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

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.width(width = 16.dp))
                AnimatedVisibility(
                    visible = showDeuceIndicator,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    DeuceIndicator()
                }
            }

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
