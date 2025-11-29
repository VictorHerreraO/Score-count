package com.soyvictorherrera.scorecount.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Generic bottom sheet picker for settings with radio button options.
 *
 * @param T The type of option being selected (must be comparable)
 * @param visible Whether the bottom sheet is visible
 * @param onDismiss Called when the bottom sheet is dismissed
 * @param title Title displayed at the top of the bottom sheet
 * @param options List of options to display
 * @param selectedOption The currently selected option
 * @param onOptionSelected Called when an option is selected (auto-dismisses)
 * @param getOptionLabel Function to get the display label for an option
 * @param getOptionDescription Optional function to get the description for an option
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
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    getOptionLabel: (T) -> String,
    getOptionDescription: ((T) -> String)? = null
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
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
                    val isSelected = option == selectedOption

                    ListItem(
                        headlineContent = { Text(getOptionLabel(option)) },
                        supportingContent =
                            getOptionDescription?.let { getDesc ->
                                { Text(getDesc(option)) }
                            },
                        leadingContent = {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onOptionSelected(option)
                                    onDismiss()
                                }
                            )
                        },
                        modifier =
                            Modifier.clickable {
                                onOptionSelected(option)
                                onDismiss()
                            }
                    )
                }
            }
        }
    }
}
