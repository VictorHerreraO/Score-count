package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState

private object CentralBarActionsDefaults {
    const val MAX_CENTRAL_BAR_ACTIONS = 3

    val gameBarActions: List<GameBarAction> =
        listOf(
            GameBarAction.SWITCH_SERVE,
            GameBarAction.UNDO,
            GameBarAction.SETTINGS,
            GameBarAction.RESET,
        )
}

@Composable
fun CentralControls(
    gameState: GameState,
    gameSettings: GameSettings,
    hasUndoHistory: Boolean,
    callbacks: CentralControlsCallbacks,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var showOverflowMenu by rememberSaveable { mutableStateOf(false) }
        val barState =
            rememberCentralBarState(
                isFinished = gameState.isFinished,
                showSwitchServe = gameSettings.markServe,
            )

        AnimatedVisibility(
            visible = gameSettings.markDeuce && gameState.isDeuce,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            DeuceIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(weight = 1f))

        // Render in reverse order so highest-priority actions appear at the bottom (closest to score cards)
        barState.visibleActions.reversed().forEach { action ->
            GameBarActionButton(
                action = action,
                modifier = Modifier.fillMaxWidth(),
                isActionEnabled = {
                    when (it) {
                        GameBarAction.UNDO -> hasUndoHistory
                        else -> true
                    }
                }
            ) {
                when (it) {
                    GameBarAction.OVERFLOW -> showOverflowMenu = true

                    else -> callbacks.forAction(action = it)
                }
            }
        }

        OverflowGameActionPicker(
            isVisible = showOverflowMenu,
            onDismiss = { showOverflowMenu = false },
            actions = barState.overflowActions.reversed(),
            onActionSelected = { action -> callbacks.forAction(action) }
        )
    }
}

@Composable
private fun rememberCentralBarState(
    isFinished: Boolean,
    showSwitchServe: Boolean,
    maxBarActions: Int = CentralBarActionsDefaults.MAX_CENTRAL_BAR_ACTIONS
): GameBarState =
    remember(isFinished, showSwitchServe) {
        if (isFinished) {
            GameBarState(
                actions = listOf(GameBarAction.START_NEW_GAME),
                maxActions = maxBarActions
            )
        } else {
            GameBarState(
                actions =
                    CentralBarActionsDefaults
                        .gameBarActions
                        .toMutableList()
                        .apply {
                            if (!showSwitchServe) {
                                remove(element = GameBarAction.SWITCH_SERVE)
                            }
                        },
                maxActions = maxBarActions
            )
        }
    }
