package com.soyvictorherrera.scorecount.ui.scorescreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.scorescreen.components.BottomBarActions
import com.soyvictorherrera.scorecount.ui.scorescreen.components.CentralControls
import com.soyvictorherrera.scorecount.ui.scorescreen.components.CentralControlsCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.DeuceIndicator
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCard
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardCallbacks
import com.soyvictorherrera.scorecount.ui.scorescreen.components.PlayerScoreCardState
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme

@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSettings by viewModel.gameSettings.collectAsState()
    val hasUndoHistory by viewModel.hasUndoHistory.collectAsState()
    val configuration = LocalConfiguration.current

    val callbacks =
        ScoreScreenCallbacks(
            onIncrement = viewModel::incrementScore,
            onDecrement = viewModel::decrementScore,
            onReset = viewModel::resetGame,
            onSwitchServe = viewModel::manualSwitchServe,
            onStartNewGame = viewModel::resetGame,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSettings = onNavigateToSettings,
            onUndo = viewModel::undoLastChange
        )

    ScoreCountTheme {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ScoreScreenLandscape(
                    gameState = gameState,
                    gameSettings = gameSettings,
                    hasUndoHistory = hasUndoHistory,
                    callbacks = callbacks
                )
            }
            else -> {
                ScoreScreenPortrait(
                    gameState = gameState,
                    gameSettings = gameSettings,
                    hasUndoHistory = hasUndoHistory,
                    callbacks = callbacks
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreenPortrait(
    gameState: GameState,
    gameSettings: GameSettings,
    hasUndoHistory: Boolean,
    callbacks: ScoreScreenCallbacks
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (gameSettings.showTitle) {
                        Text(
                            stringResource(R.string.score_table_tennis_title),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    /*
                    // TODO: hide until history revamp is completed
                    IconButton(onClick = callbacks.onNavigateToHistory) {
                        Icon(
                            Icons.Default.History,
                            contentDescription =
                                stringResource(
                                    id = R.string.cd_history
                                )
                        )
                    }
                     */
                    IconButton(onClick = callbacks.onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription =
                                stringResource(
                                    id = R.string.cd_settings
                                )
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
            )
        },
        bottomBar = {
            BottomBarActions(
                isFinished = gameState.isFinished,
                showSwitchServe = gameSettings.markServe,
                hasUndoHistory = hasUndoHistory,
                onReset = callbacks.onReset,
                onSwitchServe = callbacks.onSwitchServe,
                onStartNewGame = callbacks.onStartNewGame,
                onUndo = callbacks.onUndo
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (gameSettings.showSets) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (gameSettings.showNames) {
                            Text(
                                text = gameState.player1.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(
                            text = gameState.player1SetsWon.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.sets_separator),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (gameSettings.showNames) {
                            Text(
                                text = gameState.player2.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(
                            text = gameState.player2SetsWon.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

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
