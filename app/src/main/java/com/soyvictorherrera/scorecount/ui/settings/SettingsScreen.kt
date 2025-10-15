package com.soyvictorherrera.scorecount.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()

    // Recompose these lists whenever settings change
    val gameControls = settingsViewModel.getGameControls(settings)
    val tableTennisRules = settingsViewModel.getTableTennisRules(settings)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                stringResource(
                                    R.string.cd_navigate_back
                                )
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(stringResource(R.string.section_game_controls))
            SettingsGrid(items = gameControls, modifier = Modifier.padding(bottom = 16.dp))
            SectionHeader(stringResource(R.string.section_table_tennis_rules))
            SettingsList(items = tableTennisRules)

            Spacer(modifier = Modifier.height(16.dp)) // Add some padding at the bottom
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

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
            Box(Modifier.weight(1f)) {
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    item.icon,
                    contentDescription = stringResource(item.textRes),
                    modifier = Modifier.size(36.dp),
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
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SettingsList(items: List<SettingItemData>) {
    Column {
        items.forEach { item ->
            when (item) {
                is SettingItemData.StepperItem -> StepperSettingItem(item)
                is SettingItemData.SwitchSetting -> SwitchSettingItem(item)
                else -> {} // Should not happen in this list
            }
        }
    }
}

@Composable
fun StepperSettingItem(item: SettingItemData.StepperItem) {
    ListItem(
        headlineContent = { Text(stringResource(item.textRes)) },
        supportingContent = {
            item.subtitleRes?.let { subtitleId ->
                Text(stringResource(subtitleId))
            }
        },
        leadingContent = { Icon(item.icon, contentDescription = stringResource(item.textRes)) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    textAlign = TextAlign.Center,
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ScoreCountTheme {
        Surface {
            SettingsScreen(
                onNavigateBack = {},
                settingsViewModel =
                    SettingsViewModel(
                        settingsRepository =
                            object : SettingsRepository {
                                private val settingsFlow = MutableStateFlow(GameSettings())

                                override fun getSettings(): StateFlow<GameSettings> = settingsFlow.asStateFlow()

                                override suspend fun saveSettings(settings: GameSettings) {
                                    settingsFlow.value = settings
                                }
                            },
                        dispatcher = Dispatchers.Unconfined
                    )
            )
        }
    }
}
