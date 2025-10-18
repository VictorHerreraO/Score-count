package com.soyvictorherrera.scorecount.ui.scorescreen

/**
 * Callback actions for the ScoreScreen composables (Portrait and Landscape).
 *
 * Groups all game action callbacks to reduce parameter list complexity in screen-level composables.
 */
data class ScoreScreenCallbacks(
    val onIncrement: (Int) -> Unit,
    val onDecrement: (Int) -> Unit,
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit
)
