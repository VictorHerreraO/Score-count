package com.soyvictorherrera.scorecount.ui.scorescreen.components

data class BottomBarActionsCallbacks(
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onUndo: () -> Unit,
    val onSettings: () -> Unit,
)
