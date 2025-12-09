package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.soyvictorherrera.scorecount.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverflowGameActionPicker(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    actions: List<GameBarAction>,
    onActionSelected: (GameBarAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    BottomSheetPicker(
        visible = isVisible,
        onDismiss = onDismiss,
        sheetState = sheetState,
        title = stringResource(R.string.action_more),
        options = actions,
        onOptionSelected = {
            scope
                .launch {
                    sheetState.hide()
                    onActionSelected(it)
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismiss()
                    }
                }
        },
        optionContent = {
            ListItem(
                headlineContent = { Text(stringResource(id = it.text)) },
                leadingContent = { Icon(imageVector = it.icon, contentDescription = null) }
            )
        }
    )
}
