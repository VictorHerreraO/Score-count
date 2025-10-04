package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel,
    navController: NavController
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSettings by viewModel.gameSettings.collectAsState()

    ScoreCountTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Table Tennis", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { /* TODO: History action */ }) {
                            Icon(Icons.Default.History, contentDescription = "History")
                        }
                        IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                val currentGameState = gameState
                if (currentGameState != null) {
                    BottomBarActions(
                        isFinished = currentGameState.isFinished,
                        onReset = { viewModel.resetGame() },
                        onSwitchServe = { viewModel.manualSwitchServe() },
                        onStartNewGame = { viewModel.resetGame() } // Resetting the game for now
                    )
                }
            }
        ) { paddingValues ->
            val currentGameState = gameState
            val currentSettings = gameSettings

            if (currentGameState != null && currentSettings != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Player 1", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = currentGameState.player1SetsWon.toString(),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Text(text = ":", style = MaterialTheme.typography.headlineSmall)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Player 2", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = currentGameState.player2SetsWon.toString(),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    PlayerScoreCard(
                        playerName = "Player 1",
                        score = currentGameState.player1.score,
                        isServing = currentGameState.servingPlayerId == currentGameState.player1.id,
                        isFinished = currentGameState.isFinished,
                        onIncrement = { viewModel.incrementScore(currentGameState.player1.id) },
                        onDecrement = { viewModel.decrementScore(currentGameState.player1.id) },
                        modifier = Modifier.weight(1f)
                    )

                    PlayerScoreCard(
                        playerName = "Player 2",
                        score = currentGameState.player2.score,
                        isServing = currentGameState.servingPlayerId == currentGameState.player2.id,
                        isFinished = currentGameState.isFinished,
                        onIncrement = { viewModel.incrementScore(currentGameState.player2.id) },
                        onDecrement = { viewModel.decrementScore(currentGameState.player2.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    isServing: Boolean,
    isFinished: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onIncrement,
        enabled = !isFinished,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        playerName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (isServing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.SportsTennis,
                            contentDescription = "Serving",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = score.toString(),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 72.sp
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallIconButton(
                    onClick = onDecrement,
                    icon = Icons.Default.Remove,
                    description = "Decrement score",
                    enabled = !isFinished
                )
                SmallIconButton(
                    onClick = onIncrement,
                    icon = Icons.Default.Add,
                    description = "Increment score",
                    enabled = !isFinished
                )
            }
        }
    }
}

@Composable
fun SmallIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(32.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(icon, contentDescription = description)
    }
}

@Composable
fun BottomBarActions(
    isFinished: Boolean,
    onReset: () -> Unit,
    onSwitchServe: () -> Unit,
    onStartNewGame: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isFinished) {
                Button(
                    onClick = onStartNewGame,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Start New Game")
                }
            } else {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
                OutlinedButton(
                    onClick = onSwitchServe,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Serve")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch Serve")
                }
            }
        }
    }
}

private class FakeSettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(GameSettings())
    override fun getSettings(): Flow<GameSettings> = _settings.asStateFlow()
    override suspend fun saveSettings(settings: GameSettings) {
        _settings.value = settings
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenPreview() {
    val dummyP1 = Player(id = 1, name = "Player 1", score = 10)
    val dummyP2 = Player(id = 2, name = "Player 2", score = 10)
    val previewGameState = GameState(
        player1 = dummyP1,
        player2 = dummyP2,
        servingPlayerId = 1,
        player1SetsWon = 2,
        player2SetsWon = 1
    )
    val fakeScoreRepo = FakeScoreRepositoryPreview(initialState = previewGameState)

    val previewViewModel = ScoreViewModel(
        getGameStateUseCase = GetGameStateUseCase(fakeScoreRepo),
        incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepo),
        decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepo),
        manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepo),
        resetGameUseCase = ResetGameUseCase(fakeScoreRepo),
        undoLastActionUseCase = UndoLastActionUseCase(fakeScoreRepo),
        settingsRepository = FakeSettingsRepository()
    )
    val navController = rememberNavController()

    ScoreCountTheme {
        ScoreScreen(viewModel = previewViewModel, navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenFinishedPreview() {
    val dummyP1 = Player(id = 1, name = "Player 1", score = 0)
    val dummyP2 = Player(id = 2, name = "Player 2", score = 0)
    val previewGameState = GameState(
        player1 = dummyP1,
        player2 = dummyP2,
        servingPlayerId = 1,
        player1SetsWon = 3,
        player2SetsWon = 1,
        isFinished = true
    )
    val fakeScoreRepo = FakeScoreRepositoryPreview(initialState = previewGameState)

    val previewViewModel = ScoreViewModel(
        getGameStateUseCase = GetGameStateUseCase(fakeScoreRepo),
        incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepo),
        decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepo),
        manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepo),
        resetGameUseCase = ResetGameUseCase(fakeScoreRepo),
        undoLastActionUseCase = UndoLastActionUseCase(fakeScoreRepo),
        settingsRepository = FakeSettingsRepository()
    )
    val navController = rememberNavController()

    ScoreCountTheme {
        ScoreScreen(viewModel = previewViewModel, navController = navController)
    }
}

class FakeScoreRepositoryPreview(initialState: GameState) : ScoreRepository {
    private val _gameState = MutableStateFlow(initialState)

    override fun getGameState(): Flow<GameState> = _gameState.asStateFlow()

    override suspend fun incrementScore(playerId: Int) {
        val current = _gameState.value
        if (current.isFinished) return
        val newP1Score = if (playerId == current.player1.id) current.player1.score + 1 else current.player1.score
        val newP2Score = if (playerId == current.player2.id) current.player2.score + 1 else current.player2.score
        _gameState.value = current.copy(
            player1 = current.player1.copy(score = newP1Score),
            player2 = current.player2.copy(score = newP2Score)
        )
    }

    override suspend fun decrementScore(playerId: Int) {
        val current = _gameState.value
        if (current.isFinished) return
        val newP1Score = if (playerId == current.player1.id && current.player1.score > 0) current.player1.score - 1 else current.player1.score
        val newP2Score = if (playerId == current.player2.id && current.player2.score > 0) current.player2.score - 1 else current.player2.score
        _gameState.value = current.copy(
            player1 = current.player1.copy(score = newP1Score),
            player2 = current.player2.copy(score = newP2Score)
        )
    }

    override suspend fun manualSwitchServe() {
        val current = _gameState.value
        if (current.isFinished) return
        _gameState.value = current.copy(servingPlayerId = if (current.servingPlayerId == current.player1.id) current.player2.id else current.player1.id)
    }

    override suspend fun resetGame() {
        _gameState.value = GameState(
            player1 = Player(id = 1, name = "P1 Preview", score = 0),
            player2 = Player(id = 2, name = "P2 Preview", score = 0),
            servingPlayerId = 1
        )
    }

    override suspend fun undoLastAction() {
        // Not implemented
    }
}
