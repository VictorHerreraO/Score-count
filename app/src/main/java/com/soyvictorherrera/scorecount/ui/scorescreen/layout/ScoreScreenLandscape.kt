package com.soyvictorherrera.scorecount.ui.scorescreen.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.extension.contentVerticalPadding
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreenCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.CentralControls
import com.soyvictorherrera.scorecount.ui.scorescreen.components.MatchScoreTopAppBar
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCard
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardState
import com.soyvictorherrera.scorecount.ui.scorescreen.toGameBarActionsCallbacks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreenLandscape(
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
        }
    ) { paddingValues ->
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .contentVerticalPadding(hasTopBar = gameSettings.showSets),
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
                onIncrement = { callbacks.onIncrement(gameState.player1.id) },
                modifier = Modifier.weight(1f)
            )

            CentralControls(
                modifier = Modifier.padding(horizontal = 4.dp),
                gameState = gameState,
                gameSettings = gameSettings,
                hasUndoHistory = hasUndoHistory,
                callbacks = callbacks.toGameBarActionsCallbacks()
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
                onIncrement = { callbacks.onIncrement(gameState.player2.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
