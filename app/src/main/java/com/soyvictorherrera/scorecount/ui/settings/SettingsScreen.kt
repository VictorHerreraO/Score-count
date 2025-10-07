package com.soyvictorherrera.scorecount.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SettingItemData {
    data class ToggleItem(
        val text: String,
        val icon: ImageVector,
        val isChecked: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItemData()

    data class ActionItem(
        val text: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    ) : SettingItemData()

    data class StepperItem(
        val text: String,
        val subtitle: String? = null,
        val icon: ImageVector,
        val value: Int,
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit,
        val valueRange: IntRange
    ) : SettingItemData()

    data class SwitchSetting( // Renamed from Switch to avoid conflict
        val text: String,
        val subtitle: String? = null,
        val icon: ImageVector,
        val isChecked: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItemData()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()

    val gameControls = listOf(
        SettingItemData.ToggleItem("Show title", Icons.Filled.Title, settings.showTitle) { settingsViewModel.updateShowTitle(it) },
        SettingItemData.ToggleItem("Show names", Icons.Filled.Badge, settings.showNames) { settingsViewModel.updateShowNames(it) },
        SettingItemData.ToggleItem("Show sets", Icons.Filled.CalendarToday, settings.showSets) { settingsViewModel.updateShowSets(it) },
        SettingItemData.ToggleItem("Mark serve", Icons.Filled.PersonSearch, settings.markServe) { settingsViewModel.updateMarkServe(it) },
        SettingItemData.ToggleItem("Mark deuce", Icons.Filled.Info, settings.markDeuce) { settingsViewModel.updateMarkDeuce(it) },
        SettingItemData.ToggleItem("Keep screen on", Icons.Filled.ScreenLockPortrait, settings.keepScreenOn) { settingsViewModel.updateKeepScreenOn(it) }
    )

    val tableTennisRules = listOf(
        SettingItemData.StepperItem(
            text = "Set to",
            subtitle = "Win by 2",
            icon = Icons.Filled.EmojiEvents,
            value = settings.pointsToWinSet,
            onIncrement = { settingsViewModel.updatePointsToWinSet(settings.pointsToWinSet + 1) },
            onDecrement = { settingsViewModel.updatePointsToWinSet(settings.pointsToWinSet - 1) },
            valueRange = 1..100
        ),
        SettingItemData.StepperItem(
            text = "Match",
            subtitle = "Best of ${settings.numberOfSets} sets",
            icon = Icons.Filled.MilitaryTech,
            value = settings.numberOfSets,
            onIncrement = { settingsViewModel.updateNumberOfSets(settings.numberOfSets + 2) }, // Usually best of 1, 3, 5, etc.
            onDecrement = { settingsViewModel.updateNumberOfSets(settings.numberOfSets - 2) },
            valueRange = 1..20 // Max "best of"
        ),
        SettingItemData.StepperItem(
            text = "Serve rotation after",
            subtitle = "1 after deuce",
            icon = Icons.AutoMirrored.Filled.RotateRight,
            value = settings.serveRotationAfterPoints,
            onIncrement = { settingsViewModel.updateServeRotationAfterPoints(settings.serveRotationAfterPoints + 1) },
            onDecrement = { settingsViewModel.updateServeRotationAfterPoints(settings.serveRotationAfterPoints - 1) },
            valueRange = 1..10
        ),
        SettingItemData.SwitchSetting(
            text = "Winner serves",
            subtitle = "The winner of a game serves first in the next game",
            icon = Icons.Filled.Person,
            isChecked = settings.winnerServesNextGame,
            onToggle = { settingsViewModel.updateWinnerServesNextGame(it) }
        )
    )


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader("Game Controls & Actions")
            SettingsGrid(items = gameControls, modifier = Modifier.padding(bottom = 16.dp))

            SectionHeader("Table Tennis Rules Configuration")
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
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2
    ) {
        items.forEach { item ->
            Box(Modifier.weight(1f)) { // Use weight to ensure items take equal space up to maxItemsInEachRow
                when (item) {
                    is SettingItemData.ToggleItem -> ToggleSettingCard(item)
                    is SettingItemData.ActionItem -> ActionSettingCard(item)
                    else -> {} // Should not happen in this grid
                }
            }
        }
    }
}


@Composable
fun ToggleSettingCard(item: SettingItemData.ToggleItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { item.onToggle(!item.isChecked) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            contentColor = if (item.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    item.icon,
                    contentDescription = item.text,
                    modifier = Modifier.size(36.dp),
                    tint = if (item.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ActionSettingCard(item: SettingItemData.ActionItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { item.onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                item.icon,
                contentDescription = item.text,
                modifier = Modifier.size(36.dp)
                // Tint will be inherited from contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
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
        headlineContent = { Text(item.text) },
        supportingContent = { if (item.subtitle != null) Text(item.subtitle) },
        leadingContent = { Icon(item.icon, contentDescription = item.text) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = item.onDecrement,
                    enabled = item.value > item.valueRange.first
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrement")
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
                    Icon(Icons.Filled.Add, contentDescription = "Increment")
                }
            }
        }
    )
}

@Composable
fun SwitchSettingItem(item: SettingItemData.SwitchSetting) {
    ListItem(
        headlineContent = { Text(item.text) },
        supportingContent = { if (item.subtitle != null) Text(item.subtitle) },
        leadingContent = { Icon(item.icon, contentDescription = item.text) },
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
                settingsViewModel = SettingsViewModel(
                    settingsRepository = object : SettingsRepository {
                        private val settingsFlow = MutableStateFlow(GameSettings())
                        override fun getSettings(): StateFlow<GameSettings> = settingsFlow.asStateFlow()
                        override suspend fun saveSettings(settings: GameSettings) {
                            settingsFlow.value = settings
                        }
                    }
                )
            )
        }
    }
}
