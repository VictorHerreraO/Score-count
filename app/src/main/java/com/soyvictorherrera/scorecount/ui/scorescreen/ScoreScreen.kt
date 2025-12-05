package com.soyvictorherrera.scorecount.ui.scorescreen

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenLandscape
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenPortrait
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
