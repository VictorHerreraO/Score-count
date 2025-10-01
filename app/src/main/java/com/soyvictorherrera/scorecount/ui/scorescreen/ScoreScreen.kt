package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo // Changed import
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
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
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class) // File-level opt-in
@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel // In a real app, inject this, often with Hilt: hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    ScoreCountTheme { // Apply your app's theme
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Table Tennis", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { /* TODO: History action */ }) {
                            Icon(Icons.Outlined.History, contentDescription = "History")
                        }
                        IconButton(onClick = { /* TODO: Settings action */ }) {
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
            gameState?.let { currentGameState ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PlayerScoreCard(
                        playerName = currentGameState.player1.name,
                        score = currentGameState.player1.score,
                        isServing = currentGameState.servingPlayerId == currentGameState.player1.id,
                        onIncrement = { viewModel.incrementScore(currentGameState.player1.id) },
                        onDecrement = { viewModel.decrementScore(currentGameState.player1.id) },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { viewModel.switchServe() },
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
                        playerName = currentGameState.player2.name,
                        score = currentGameState.player2.score,
                        isServing = currentGameState.servingPlayerId == currentGameState.player2.id,
                        onIncrement = { viewModel.incrementScore(currentGameState.player2.id) },
                        onDecrement = { viewModel.decrementScore(currentGameState.player2.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            } ?: run {
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

@Preview(showBackground = true)
@Composable
fun ScoreScreenPreview() {
    val dummyP1 = Player(id = 1, name = "Player 1", score = 5)
    val dummyP2 = Player(id = 2, name = "Player 2", score = 3)
    val dummyGameState = GameState(player1 = dummyP1, player2 = dummyP2, servingPlayerId = 1, isFinished = false)

    val previewViewModel = ScoreViewModel(
        getGameStateUseCase = GetGameStateUseCase(FakeScoreRepository()),
        incrementScoreUseCase = IncrementScoreUseCase(FakeScoreRepository()),
        decrementScoreUseCase = DecrementScoreUseCase(FakeScoreRepository()),
        switchServeUseCase = SwitchServeUseCase(FakeScoreRepository()),
        resetGameUseCase = ResetGameUseCase(FakeScoreRepository()),
        undoLastActionUseCase = UndoLastActionUseCase(FakeScoreRepository())
    )

    ScoreCountTheme {
        ScoreScreenContentForPreview(dummyGameState)
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Added OptIn here
@Composable
fun ScoreScreenContentForPreview(gameState: GameState) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Table Tennis Preview") }) },
        bottomBar = { BottomBarActions(onReset = {}, onUndo = {}) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PlayerScoreCard(
                playerName = gameState.player1.name,
                score = gameState.player1.score,
                isServing = gameState.servingPlayerId == gameState.player1.id,
                onIncrement = { },
                onDecrement = { },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { }, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) { // This is line 248 if previous OptIn is line 229
                Icon(Icons.Filled.SwapHoriz, contentDescription = "Switch Serve")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Switch Serve")
            }
            PlayerScoreCard(
                playerName = gameState.player2.name,
                score = gameState.player2.score,
                isServing = gameState.servingPlayerId == gameState.player2.id,
                onIncrement = { },
                onDecrement = { },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Fake repository for preview
class FakeScoreRepository : ScoreRepository {
    private val _gameState = MutableStateFlow(
        GameState(
            player1 = Player(id = 1, name = "P1 Preview", score = 0),
            player2 = Player(id = 2, name = "P2 Preview", score = 0),
            servingPlayerId = 1,
            isFinished = false
        )
    )

    override fun getGameState(): Flow<GameState> = flowOf(_gameState.value)

    override suspend fun incrementScore(playerId: Int) {
        val current = _gameState.value
        if (playerId == current.player1.id) {
            _gameState.value = current.copy(player1 = current.player1.copy(score = current.player1.score + 1))
        } else if (playerId == current.player2.id) {
            _gameState.value = current.copy(player2 = current.player2.copy(score = current.player2.score + 1))
        }
    }

    override suspend fun decrementScore(playerId: Int) {
        val current = _gameState.value
        if (playerId == current.player1.id && current.player1.score > 0) {
            _gameState.value = current.copy(player1 = current.player1.copy(score = current.player1.score - 1))
        } else if (playerId == current.player2.id && current.player2.score > 0) {
            _gameState.value = current.copy(player2 = current.player2.copy(score = current.player2.score - 1))
        }
    }

    override suspend fun switchServe() {
        val current = _gameState.value
        _gameState.value = current.copy(servingPlayerId = if (current.servingPlayerId == current.player1.id) current.player2.id else current.player1.id)
    }

    override suspend fun resetGame() {
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
