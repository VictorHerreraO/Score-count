package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.soyvictorherrera.scorecount.R

private object BottomBarActionsDefaults {
    const val MAX_BOTTOM_BAR_ACTIONS = 3

    val BottomBarActions: List<BottomBarAction> =
        listOf(
            BottomBarAction.UNDO,
            BottomBarAction.SWITCH_SERVE,
            BottomBarAction.RESET,
            BottomBarAction.SETTINGS,
        )
}

private enum class BottomBarAction(
    val icon: ImageVector?,
    @get:StringRes val text: Int,
) {
    OVERFLOW(icon = Icons.Default.MoreHoriz, text = R.string.action_more),
    RESET(icon = Icons.Default.RestartAlt, text = R.string.action_reset),
    SETTINGS(icon = Icons.Default.Settings, text = R.string.action_settings),
    START_NEW_GAME(icon = null, text = R.string.action_start_new_game),
    SWITCH_SERVE(icon = Icons.Default.SwapHoriz, text = R.string.action_switch_serve),
    UNDO(icon = Icons.AutoMirrored.Default.Undo, text = R.string.action_undo),
}

private data class BottomBarState(
    val actions: List<BottomBarAction>
) {
    val visibleActions: List<BottomBarAction> by lazy {
        if (actions.size > BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS) {
            actions
                .take(BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS.dec())
                .plus(BottomBarAction.OVERFLOW)
        } else {
            actions
        }
    }

    val overflowActions: List<BottomBarAction> by lazy {
        actions.drop(BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS.dec())
    }
}

@Composable
fun BottomBarActions(
    isFinished: Boolean,
    showSwitchServe: Boolean,
    hasUndoHistory: Boolean,
    callbacks: BottomBarActionsCallbacks,
) {
    var showOverflowMenu by remember { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val barState = rememberBottomBarState(isFinished, showSwitchServe)
    val showExpandedButton = windowSizeClass.isWidthAtLeastBreakpoint(widthDpBreakpoint = WIDTH_DP_MEDIUM_LOWER_BOUND)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            barState.visibleActions.forEach { action ->
                OutlinedButton(
                    onClick = {
                        when (action) {
                            BottomBarAction.OVERFLOW -> showOverflowMenu = true
                            else -> callbacks.forAction(action = action)
                        }
                    },
                    enabled =
                        when (action) {
                            BottomBarAction.UNDO -> hasUndoHistory
                            else -> true
                        },
                    modifier = Modifier.weight(1f),
                ) {
                    if (action.icon != null) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = stringResource(id = action.text)
                        )
                    } else {
                        Text(stringResource(id = action.text))
                    }
                }
            }
        }
    }

    OverflowActionPicker(
        visible = showOverflowMenu,
        onDismiss = { showOverflowMenu = false },
        actions = barState.overflowActions,
        onActionSelected = { action -> callbacks.forAction(action) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverflowActionPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    actions: List<BottomBarAction>,
    onActionSelected: (BottomBarAction) -> Unit
) {
    BottomSheetPicker(
        visible = visible,
        onDismiss = onDismiss,
        title = stringResource(R.string.action_more),
        options = actions,
        onOptionSelected = onActionSelected,
        optionContent = {
            val leadingIconContent =
                it.icon?.let { icon ->
                    @Composable { Icon(icon, null) }
                }

            ListItem(
                headlineContent = {
                    Text(stringResource(it.text))
                },
                leadingContent = leadingIconContent
            )
        }
    )
}

@Composable
private fun rememberBottomBarState(
    isFinished: Boolean,
    showSwitchServe: Boolean,
): BottomBarState =
    remember(isFinished, showSwitchServe) {
        if (isFinished) {
            BottomBarState(actions = listOf(BottomBarAction.START_NEW_GAME))
        } else {
            BottomBarState(
                actions =
                    BottomBarActionsDefaults
                        .BottomBarActions
                        .toMutableList()
                        .apply {
                            if (!showSwitchServe) {
                                remove(BottomBarAction.SWITCH_SERVE)
                            }
                        }
            )
        }
    }

/**
 * A helper function to map a [BottomBarAction] to its corresponding callback function
 * defined in the [BottomBarActionsCallbacks] class. This simplifies the `onClick`
 * logic within the composable.
 *
 * @param action The [BottomBarAction] that was triggered by the user.
 */
private fun BottomBarActionsCallbacks.forAction(action: BottomBarAction) {
    when (action) {
        BottomBarAction.OVERFLOW -> { /* Should not happen */ }
        BottomBarAction.RESET -> onReset()
        BottomBarAction.SETTINGS -> onSettings()
        BottomBarAction.START_NEW_GAME -> onStartNewGame()
        BottomBarAction.SWITCH_SERVE -> onSwitchServe()
        BottomBarAction.UNDO -> onUndo()
    }
}
