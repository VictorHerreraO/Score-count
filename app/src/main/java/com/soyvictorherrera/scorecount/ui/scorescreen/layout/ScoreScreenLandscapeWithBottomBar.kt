package com.soyvictorherrera.scorecount.ui.scorescreen.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreenCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.BottomBarActions
import com.soyvictorherrera.scorecount.ui.scorescreen.components.BottomBarActionsCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.DeuceIndicator
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCard
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardState

private object ScoreScreenDefaults {
    const val ALPHA_PRIMARY = 1F
    const val ALPHA_SECONDARY = 0.75F
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ScoreScreenLandscapeWithBottomBar(
    gameState: GameState,
    gameSettings: GameSettings,
    hasUndoHistory: Boolean,
    callbacks: ScoreScreenCallbacks
) {
    Scaffold(
        topBar = {
            if (gameSettings.showSets) {
                CenterAlignedTopAppBar(
                    title = {
                        HorizontalMatchScore(
                            gameState = gameState,
                            dividerContent = {
                                GameSets(
                                    matchNumber = gameState.currentSet,
                                    numberOfSets = gameSettings.numberOfSets,
                                )
                            }
                        )
                    }
                )
            }
        },
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
                    .fillMaxWidth()
                    .padding(all = 16.dp)
                    .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
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
                modifier = Modifier.weight(weight = 1f)
            )

            AnimatedVisibility(
                visible = gameSettings.markDeuce && gameState.isDeuce,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                DeuceIndicator()
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

@Composable
fun GameSets(
    matchNumber: Int,
    numberOfSets: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Max)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = "MATCH $matchNumber",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .weight(1f)
                        .alpha(0.85f),
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = "Best of $numberOfSets",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun HorizontalMatchScore(
    gameState: GameState,
    modifier: Modifier = Modifier,
    dividerContent: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier,
    ) {
        val isPlayer1Serving = gameState.servingPlayerId == gameState.player1.id
        val player1ScoreAlpha =
            with(ScoreScreenDefaults) {
                if (isPlayer1Serving) ALPHA_PRIMARY else ALPHA_SECONDARY
            }
        val player2ScoreAlpha =
            with(ScoreScreenDefaults) {
                if (!isPlayer1Serving) ALPHA_PRIMARY else ALPHA_SECONDARY
            }

        Text(
            text = gameState.player1SetsWon.toString(),
            style =
                MaterialTheme.typography.displaySmall.copy(
                    fontWeight = if (isPlayer1Serving) FontWeight.Bold else FontWeight.Normal
                ),
            modifier = Modifier.alpha(alpha = player1ScoreAlpha)
        )

        dividerContent()

        Text(
            text = gameState.player2SetsWon.toString(),
            style =
                MaterialTheme.typography.displaySmall.copy(
                    fontWeight = if (!isPlayer1Serving) FontWeight.Bold else FontWeight.Normal
                ),
            modifier = Modifier.alpha(alpha = player2ScoreAlpha)
        )
    }
}
