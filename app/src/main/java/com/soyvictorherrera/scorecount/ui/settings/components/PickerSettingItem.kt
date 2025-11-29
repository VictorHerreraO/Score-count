package com.soyvictorherrera.scorecount.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.soyvictorherrera.scorecount.ui.settings.SettingItemData

/**
 * Setting item that displays current selection and opens a bottom sheet picker on click.
 */
@Composable
fun PickerSettingItem(item: SettingItemData.PickerSetting) {
    ListItem(
        headlineContent = { Text(stringResource(item.textRes)) },
        supportingContent = { Text(stringResource(item.currentValueLabelRes)) },
        leadingContent = { Icon(item.icon, contentDescription = stringResource(item.textRes)) },
        modifier = Modifier.clickable { item.onClick() }
    )
}
