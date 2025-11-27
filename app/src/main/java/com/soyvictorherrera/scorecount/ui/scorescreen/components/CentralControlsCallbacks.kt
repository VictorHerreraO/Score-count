package com.soyvictorherrera.scorecount.ui.scorescreen.components

/**
 * Callback actions for the CentralControls component.
 *
 * Groups all action callbacks to reduce parameter list complexity.
 */
data class CentralControlsCallbacks(
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit,
    val onUndo: () -> Unit
)
