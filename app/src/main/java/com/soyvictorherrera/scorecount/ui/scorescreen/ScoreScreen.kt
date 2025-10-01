package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import com.soyvictorherrera.scorecount.ui.Screen
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class) // File-level opt-in
@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel,
    navController: NavController
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSettings by viewModel.gameSettings.collectAsState()

    ScoreCountTheme { // Apply your app's theme
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (gameSettings?.showTitle == true) {
                            Text("Table Tennis", fontWeight = FontWeight.Bold)
                        } else {
                            Text("", fontWeight = FontWeight.Bold) // Empty text if title is hidden
                        }
                    },
                    actions = {
                        if (gameSettings?.showPreviousSets == true) {
                            IconButton(onClick = { /* TODO: History action */ }) {
                                Icon(Icons.Outlined.History, contentDescription = "History")
                            }
                        }
                        IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                            Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            bottomBar = {
                BottomBarActions(
                    onReset = { viewModel.resetGame() },
                    onUndo = { viewModel.undoLastAction() }
                )
            }
        ) { paddingValues ->
            val currentGameState = gameState
            val currentSettings = gameSettings

            if (currentGameState != null && currentSettings != null) {
                val isDeuce = currentSettings.markDeuce &&
                        currentGameState.player1.score >= currentSettings.pointsToWinSet - 1 &&
                        currentGameState.player2.score >= currentSettings.pointsToWinSet - 1 &&
                        currentGameState.player1.score == currentGameState.player2.score

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PlayerScoreCard(
                        playerName = if (currentSettings.showNames) currentGameState.player1.name else "Player 1",
                        score = currentGameState.player1.score,
                        isServing = currentSettings.markServe && currentGameState.servingPlayerId == currentGameState.player1.id,
                        isDeuce = isDeuce,
                        onIncrement = { viewModel.incrementScore(currentGameState.player1.id) },
                        onDecrement = { viewModel.decrementScore(currentGameState.player1.id) },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { viewModel.manualSwitchServe() }, // Updated to call manualSwitchServe
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = "Switch Serve")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch Serve")
                    }

                    PlayerScoreCard(
                        playerName = if (currentSettings.showNames) currentGameState.player2.name else "Player 2",
                        score = currentGameState.player2.score,
                        isServing = currentSettings.markServe && currentGameState.servingPlayerId == currentGameState.player2.id,
                        isDeuce = isDeuce,
                        onIncrement = { viewModel.incrementScore(currentGameState.player2.id) },
                        onDecrement = { viewModel.decrementScore(currentGameState.player2.id) },
                        modifier = Modifier.weight(1f)
                    )
                    // TODO: Display sets information if currentSettings.showSets is true
                    if (currentSettings.showSets) {
                        Text(
                            text = "Sets: ${currentGameState.player1SetsWon} - ${currentGameState.player2SetsWon}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    isServing: Boolean,
    isDeuce: Boolean, // Added isDeuce parameter
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        playerName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    if (isServing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.SportsTennis,
                            contentDescription = "Serving",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isDeuce) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "DEUCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = score.toString(),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 72.sp
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallIconButton(onClick = onDecrement, icon = Icons.Filled.Remove, description = "Decrement score")
                SmallIconButton(onClick = onIncrement, icon = Icons.Filled.Add, description = "Increment score")
            }
        }
    }
}

@Composable
fun SmallIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(icon, contentDescription = description)
    }
}

@Composable
fun BottomBarActions(onReset: () -> Unit, onUndo: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Filled.RestartAlt, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset")
            }
            OutlinedButton(
                onClick = onUndo,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo") // Changed icon
                Spacer(modifier = Modifier.width(8.dp))
                Text("Undo")
            }
        }
    }
}

// Dummy repository for preview - needs SettingsRepository for GameSettings
private class FakeSettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(GameSettings(pointsToWinSet = 11, markDeuce = true, showSets = true))
    override fun getSettings(): Flow<GameSettings> = _settings.asStateFlow() // Corrected
    override suspend fun saveSettings(settings: GameSettings) { 
        _settings.value = settings
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenPreview() {
    val dummyP1 = Player(id = 1, name = "Player 1", score = 10)
    val dummyP2 = Player(id = 2, name = "Player 2", score = 10) // Scores for deuce preview
    val previewGameState = GameState(
        player1 = dummyP1, 
        player2 = dummyP2, 
        servingPlayerId = 1, 
        player1SetsWon = 1,
        player2SetsWon = 0,
        isFinished = false
    )
    val fakeScoreRepo = FakeScoreRepositoryPreview(initialState = previewGameState) // Use preview-specific fake repo

    val previewViewModel = ScoreViewModel(
        getGameStateUseCase = GetGameStateUseCase(fakeScoreRepo),
        incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepo),
        decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepo),
        manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepo), // Corrected
        resetGameUseCase = ResetGameUseCase(fakeScoreRepo),
        undoLastActionUseCase = UndoLastActionUseCase(fakeScoreRepo),
        settingsRepository = FakeSettingsRepository() // Added fake settings repository
    )
    val navController = rememberNavController()

    ScoreCountTheme {
        ScoreScreen(viewModel = previewViewModel, navController = navController)
    }
}

// Fake repository for preview, allowing initial state for specific scenarios
class FakeScoreRepositoryPreview(initialState: GameState) : ScoreRepository {
    private val _gameState = MutableStateFlow(initialState)

    override fun getGameState(): Flow<GameState> = _gameState.asStateFlow()

    override suspend fun incrementScore(playerId: Int) {
        val current = _gameState.value
        val newP1Score = if (playerId == current.player1.id) current.player1.score + 1 else current.player1.score
        val newP2Score = if (playerId == current.player2.id) current.player2.score + 1 else current.player2.score
        // Simplified increment, doesn't handle game/set logic for preview
        _gameState.value = current.copy(
            player1 = current.player1.copy(score = newP1Score),
            player2 = current.player2.copy(score = newP2Score)
        )
    }

    override suspend fun decrementScore(playerId: Int) {
        val current = _gameState.value
        val newP1Score = if (playerId == current.player1.id && current.player1.score > 0) current.player1.score - 1 else current.player1.score
        val newP2Score = if (playerId == current.player2.id && current.player2.score > 0) current.player2.score - 1 else current.player2.score
        _gameState.value = current.copy(
            player1 = current.player1.copy(score = newP1Score),
            player2 = current.player2.copy(score = newP2Score)
        )
    }

    override suspend fun manualSwitchServe() {
        val current = _gameState.value
        _gameState.value = current.copy(servingPlayerId = if (current.servingPlayerId == current.player1.id) current.player2.id else current.player1.id)
    }

    override suspend fun resetGame() {
        // For preview, reset might just go back to the specific initial state or a generic one
        _gameState.value = GameState(
            player1 = Player(id = 1, name = "P1 Preview", score = 0),
            player2 = Player(id = 2, name = "P2 Preview", score = 0),
            servingPlayerId = 1,
            isFinished = false
        )
    }

    override suspend fun undoLastAction() {
        // Not implemented for this simple fake
    }
}
