package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A generic modal bottom sheet that displays a list of selectable options.
 * It's designed to be flexible, allowing custom content for each option.
 *
 * @param T The type of the data associated with each option.
 * @param visible `true` to show the bottom sheet, `false` to hide it.
 * @param onDismiss Callback invoked when the user requests to dismiss the sheet
 *              (e.g., by tapping the scrim or back button).
 * @param title The title text displayed at the top of the bottom sheet.
 * @param options A list of items of type [T] to be displayed as options.
 * @param onOptionSelected Callback invoked with the selected option [T] when the user taps an item.
 *              The sheet will automatically attempt to hide after this is called.
 * @param sheetState The state of the modal bottom sheet, which can be used to control it programmatically.
 * @param optionContent A composable lambda that defines the content for each individual option.
 *              It's invoked for each item in the [options] list, receiving the item `T` as a parameter.
 *              The entire area of this content will be clickable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Suppressed: Generic reusable component requires these parameters for flexibility
@Suppress("LongParameterList")
fun <T> BottomSheetPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    optionContent: @Composable (T) -> Unit,
) {
    if (visible) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .verticalScroll(state = rememberScrollState())
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                // Options
                options.forEach { option ->
                    Box(
                        modifier = Modifier.clickable { onOptionSelected(option) }
                    ) { optionContent(option) }
                }
            }
        }
    }
}
