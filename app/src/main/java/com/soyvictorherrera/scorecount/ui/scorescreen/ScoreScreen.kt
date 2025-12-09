package com.soyvictorherrera.scorecount.ui.scorescreen

import android.content.res.Configuration
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenLandscape
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenLandscapeWithBottomBar
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
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

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
                val useBottomBarLayout =
                    windowSizeClass.isHeightAtLeastBreakpoint(heightDpBreakpoint = HEIGHT_DP_MEDIUM_LOWER_BOUND)
                if (useBottomBarLayout) {
                    ScoreScreenLandscapeWithBottomBar(
                        gameState = gameState,
                        gameSettings = gameSettings,
                        hasUndoHistory = hasUndoHistory,
                        callbacks = callbacks
                    )
                } else {
                    ScoreScreenLandscape(
                        gameState = gameState,
                        gameSettings = gameSettings,
                        hasUndoHistory = hasUndoHistory,
                        callbacks = callbacks
                    )
                }
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
