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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents // Trophy icon for "Set to"
import androidx.compose.material.icons.filled.MilitaryTech // Medal icon for "Match"
import androidx.compose.material.icons.filled.Person // For "Winner serves"
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Title // Keep for "Show title" action button
import androidx.compose.material.icons.outlined.AccountBox // For "Show names"
import androidx.compose.material.icons.outlined.History // For "Show previous sets" (outlined)
import androidx.compose.material.icons.outlined.ReportProblem // For "Mark deuce"
import androidx.compose.material.icons.outlined.SportsTennis // For "Mark serve" (outlined)
import androidx.compose.material.icons.outlined.Timer // For "Show sets"
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
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreViewModel
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
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()

    val gameControls = listOf(
        SettingItemData.ActionItem("Switch serve", Icons.Filled.SwapHoriz) { scoreViewModel.manualSwitchServe() },
        SettingItemData.ToggleItem("Show title", Icons.Filled.Title, settings.showTitle) { settingsViewModel.updateShowTitle(it) },
        SettingItemData.ToggleItem("Show names", Icons.Outlined.AccountBox, settings.showNames) { settingsViewModel.updateShowNames(it) },
        SettingItemData.ToggleItem("Show sets", Icons.Outlined.Timer, settings.showSets) { settingsViewModel.updateShowSets(it) },
        SettingItemData.ToggleItem("Mark serve", Icons.Outlined.SportsTennis, settings.markServe) { settingsViewModel.updateMarkServe(it) },
        SettingItemData.ToggleItem("Mark deuce", Icons.Outlined.ReportProblem, settings.markDeuce) { settingsViewModel.updateMarkDeuce(it) },
        SettingItemData.ActionItem("Show previous sets", Icons.Outlined.History) { /* TODO: Implement History action */ }
    )

    val tableTennisRules = listOf(
        SettingItemData.StepperItem(
            text = "Set to",
            subtitle = if (settings.winByTwo) "Win by 2" else null,
            icon = Icons.Filled.EmojiEvents,
            value = settings.pointsToWinSet,
            onIncrement = { settingsViewModel.updatePointsToWinSet(settings.pointsToWinSet + 1) },
            onDecrement = { settingsViewModel.updatePointsToWinSet(settings.pointsToWinSet - 1) },
            valueRange = 1..100
        ),
        SettingItemData.SwitchSetting( // Assuming WinByTwo is a simple switch now
            text = "Win by two",
            icon = Icons.Filled.EmojiEvents, // Can use the same icon or a related one
            isChecked = settings.winByTwo,
            onToggle = { settingsViewModel.updateWinByTwo(it) }
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
            subtitle = if (settings.serveChangeAfterDeuce > 0) "${settings.serveChangeAfterDeuce} after deuce" else null,
            icon = Icons.AutoMirrored.Filled.RotateRight,
            value = settings.serveRotationAfterPoints,
            onIncrement = { settingsViewModel.updateServeRotationAfterPoints(settings.serveRotationAfterPoints + 1) },
            onDecrement = { settingsViewModel.updateServeRotationAfterPoints(settings.serveRotationAfterPoints - 1) },
            valueRange = 1..10
        ),
        SettingItemData.StepperItem( // Added for serveChangeAfterDeuce
            text = "Serve change (deuce)",
            subtitle = "Points before switch",
            icon = Icons.AutoMirrored.Filled.RotateRight,
            value = settings.serveChangeAfterDeuce,
            onIncrement = { settingsViewModel.updateServeChangeAfterDeuce(settings.serveChangeAfterDeuce + 1) },
            onDecrement = { settingsViewModel.updateServeChangeAfterDeuce(settings.serveChangeAfterDeuce - 1) },
            valueRange = 0..5 // 0 to disable
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
            if (item.isChecked) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
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
            contentColor = MaterialTheme.colorScheme.primary // Action items usually have primary color for icon/text
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


// Preview
class FakeSettingsRepositoryPreview : SettingsRepository {
    private val _settings = MutableStateFlow(
        GameSettings(
//            player1Name = "Player 1", // GameSettings does not have player names directly
//            player2Name = "Player 2",
            pointsToWinSet = 11,
            winByTwo = true,
            numberOfSets = 3,
            serveRotationAfterPoints = 2,
            serveChangeAfterDeuce = 1,
            winnerServesNextGame = false,
            showTitle = true,
            showNames = true,
            showSets = true,
            markServe = true,
            markDeuce = true,
            showPreviousSets = true
        )
    )

    override fun getSettings(): StateFlow<GameSettings> = _settings.asStateFlow()
    override suspend fun saveSettings(settings: GameSettings) {
        _settings.value = settings
    }
}

class FakeScoreRepositoryPreview : ScoreRepository {
    private val _gameState = MutableStateFlow(
        GameState(
            player1 = Player(id = 1, name = "P1 Prev", score = 10),
            player2 = Player(id = 2, name = "P2 Prev", score = 10),
            servingPlayerId = 1,
            player1SetsWon = 0,
            player2SetsWon = 1,
            isFinished = false
        )
    )
    override fun getGameState(): StateFlow<GameState> = _gameState.asStateFlow()
    override suspend fun incrementScore(playerId: Int) {}
    override suspend fun decrementScore(playerId: Int) {}
    override suspend fun manualSwitchServe() {
         _gameState.value = _gameState.value.copy(servingPlayerId = if (_gameState.value.servingPlayerId == 1) 2 else 1)
    }
    override suspend fun resetGame() {}
    override suspend fun undoLastAction() {}
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ScoreCountTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val fakeSettingsRepo = FakeSettingsRepositoryPreview()
            val fakeScoreRepo = FakeScoreRepositoryPreview()
            SettingsScreen(
                onNavigateBack = {},
                scoreViewModel = ScoreViewModel(
                    settingsRepository = fakeSettingsRepo,
                    getGameStateUseCase = com.soyvictorherrera.scorecount.domain.usecase.GetGameStateUseCase(fakeScoreRepo),
                    incrementScoreUseCase = com.soyvictorherrera.scorecount.domain.usecase.IncrementScoreUseCase(fakeScoreRepo),
                    decrementScoreUseCase = com.soyvictorherrera.scorecount.domain.usecase.DecrementScoreUseCase(fakeScoreRepo),
                    manualSwitchServeUseCase = com.soyvictorherrera.scorecount.domain.usecase.ManualSwitchServeUseCase(fakeScoreRepo),
                    resetGameUseCase = com.soyvictorherrera.scorecount.domain.usecase.ResetGameUseCase(fakeScoreRepo),
                    undoLastActionUseCase = com.soyvictorherrera.scorecount.domain.usecase.UndoLastActionUseCase(fakeScoreRepo)
                ),
                settingsViewModel = SettingsViewModel(fakeSettingsRepo)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun ToggleSettingCardPreviewChecked() {
    ScoreCountTheme {
        ToggleSettingCard(
            SettingItemData.ToggleItem("Show Title", Icons.Filled.Title, true) {}
        )
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun ToggleSettingCardPreviewUnchecked() {
    ScoreCountTheme {
        ToggleSettingCard(
            SettingItemData.ToggleItem("Show Title", Icons.Filled.Title, false) {}
        )
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun ActionSettingCardPreview() {
    ScoreCountTheme {
        ActionSettingCard(
            SettingItemData.ActionItem("Switch Serve", Icons.Filled.SwapHoriz) {}
        )
    }
}
