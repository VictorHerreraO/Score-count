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

fun CentralControlsCallbacks.forAction(action: GameBarAction) {
    when (action) {
        // Should not happen
        GameBarAction.OVERFLOW -> Unit
        GameBarAction.RESET -> onReset()
        GameBarAction.SETTINGS -> onNavigateToSettings()
        GameBarAction.START_NEW_GAME -> onStartNewGame()
        GameBarAction.SWITCH_SERVE -> onSwitchServe()
        GameBarAction.UNDO -> onUndo()
    }
}
