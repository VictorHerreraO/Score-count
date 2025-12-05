package com.soyvictorherrera.scorecount.ui.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.ui.settings.SettingItemData

@Composable
fun StepperSettingItem(item: SettingItemData.StepperItem) {
    ListItem(
        headlineContent = { Text(stringResource(item.textRes)) },
        supportingContent = {
            item.subtitleRes?.let { subtitleId ->
                Text(stringResource(subtitleId, item.value))
            }
        },
        leadingContent = { Icon(item.icon, contentDescription = stringResource(item.textRes)) },
        trailingContent = {
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                IconButton(
                    onClick = item.onDecrement,
                    enabled = item.value > item.valueRange.first
                ) {
                    Icon(
                        Icons.Filled.Remove,
                        contentDescription = stringResource(R.string.cd_decrement)
                    )
                }
                Text(
                    text = item.value.toString(),
                    modifier = Modifier.width(30.dp), // Fixed width for number
                    textAlign = TextAlign.Companion.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = item.onIncrement,
                    enabled = item.value < item.valueRange.last
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.cd_increment)
                    )
                }
            }
        }
    )
}
