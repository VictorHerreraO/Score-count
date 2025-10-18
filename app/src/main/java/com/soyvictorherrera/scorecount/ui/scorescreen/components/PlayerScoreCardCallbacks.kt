package com.soyvictorherrera.scorecount.ui.scorescreen.components

/**
 * Callback actions for the PlayerScoreCard component.
 *
 * Groups increment and decrement callbacks to reduce parameter list complexity.
 */
data class PlayerScoreCardCallbacks(
    val onIncrement: () -> Unit,
    val onDecrement: () -> Unit
)
