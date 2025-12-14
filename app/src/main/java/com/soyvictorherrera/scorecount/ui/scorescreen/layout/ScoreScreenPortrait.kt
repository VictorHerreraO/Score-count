package com.soyvictorherrera.scorecount.ui.scorescreen.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.extension.contentVerticalPadding
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreenCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.BottomBarActions
import com.soyvictorherrera.scorecount.ui.scorescreen.components.DeuceIndicator
import com.soyvictorherrera.scorecount.ui.scorescreen.components.MatchScoreTopAppBar
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCard
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardState
import com.soyvictorherrera.scorecount.ui.scorescreen.toGameBarActionsCallbacks

@Composable
fun ScoreScreenPortrait(
    gameState: GameState,
    gameSettings: GameSettings,
    hasUndoHistory: Boolean,
    callbacks: ScoreScreenCallbacks
) {
    Scaffold(
        topBar = {
            if (gameSettings.showSets) {
                MatchScoreTopAppBar(
                    gameState = gameState,
                    gameSettings = gameSettings
                )
            }
        },
        bottomBar = {
            BottomBarActions(
                isFinished = gameState.isFinished,
                showSwitchServe = gameSettings.markServe,
                hasUndoHistory = hasUndoHistory,
                callbacks = callbacks.toGameBarActionsCallbacks()
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .contentVerticalPadding(hasTopBar = gameSettings.showSets),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                onIncrement = { callbacks.onIncrement(gameState.player1.id) },
                modifier = Modifier.weight(1f)
            )

            if (gameSettings.markDeuce && gameState.isDeuce) {
                DeuceIndicator(modifier = Modifier.fillMaxWidth())
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
                onIncrement = { callbacks.onIncrement(gameState.player2.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
