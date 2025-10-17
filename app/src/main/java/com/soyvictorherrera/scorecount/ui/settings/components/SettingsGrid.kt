package com.soyvictorherrera.scorecount.ui.settings.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.ui.settings.SettingItemData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsGrid(
    items: List<SettingItemData>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
    ) {
        items.forEach { item ->
            Box(Modifier.Companion.weight(1f)) {
                // Use weight to ensure items take equal space up to maxItemsInEachRow
                when (item) {
                    is SettingItemData.ToggleItem -> ToggleSettingCard(item)
                    else -> {} // Only ToggleItems are used in the grid
                }
            }
        }
    }
}

@Composable
fun ToggleSettingCard(item: SettingItemData.ToggleItem) {
    Card(
        onClick = { item.onToggle(!item.isChecked) },
        modifier =
            Modifier
                .fillMaxWidth()
                .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                contentColor =
                    if (item.isChecked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(
                                alpha = 0.7f
                            )
                    }
            )
    ) {
        Box(modifier = Modifier.Companion.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                Icon(
                    item.icon,
                    contentDescription = stringResource(item.textRes),
                    modifier = Modifier.Companion.size(36.dp),
                    tint =
                        if (item.isChecked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                                .copy(
                                    alpha = 0.7f
                                )
                        }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(item.textRes),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Companion.Center
                )
            }
        }
    }
}
