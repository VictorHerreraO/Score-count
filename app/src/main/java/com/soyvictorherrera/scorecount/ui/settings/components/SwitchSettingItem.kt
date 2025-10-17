package com.soyvictorherrera.scorecount.ui.settings.components

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.soyvictorherrera.scorecount.ui.settings.SettingItemData

@Composable
fun SwitchSettingItem(item: SettingItemData.SwitchSetting) {
    ListItem(
        headlineContent = { Text(stringResource(item.textRes)) },
        supportingContent = {
            if (item.subtitleRes != null) {
                Text(stringResource(item.subtitleRes))
            }
        },
        leadingContent = { Icon(item.icon, contentDescription = stringResource(item.textRes)) },
        trailingContent = {
            Switch(
                checked = item.isChecked,
                onCheckedChange = item.onToggle
            )
        }
    )
}
