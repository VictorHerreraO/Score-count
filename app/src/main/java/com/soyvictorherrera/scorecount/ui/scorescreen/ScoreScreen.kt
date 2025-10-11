package com.soyvictorherrera.scorecount.ui.scorescreen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.DecrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.IncrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ManualSwitchServeUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ResetGameUseCase
import com.soyvictorherrera.scorecount.domain.usecase.SaveMatchUseCase
import com.soyvictorherrera.scorecount.ui.Screen
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel,
    navController: NavController
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSettings by viewModel.gameSettings.collectAsState()
    val configuration = LocalConfiguration.current

    ScoreCountTheme {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ScoreScreenLandscape(
                    navController = navController,
                    gameState = gameState,
                    gameSettings = gameSettings,
                    onIncrement = viewModel::incrementScore,
                    onDecrement = viewModel::decrementScore,
                    onReset = viewModel::resetGame,
                    onSwitchServe = viewModel::manualSwitchServe,
                    onStartNewGame = viewModel::resetGame
                )
            }
            else -> {
                ScoreScreenPortrait(
                    navController = navController,
                    gameState = gameState,
                    gameSettings = gameSettings,
                    onIncrement = viewModel::incrementScore,
                    onDecrement = viewModel::decrementScore,
                    onReset = viewModel::resetGame,
                    onSwitchServe = viewModel::manualSwitchServe,
                    onStartNewGame = viewModel::resetGame
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreenPortrait(
    navController: NavController,
    gameState: GameState,
    gameSettings: GameSettings,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
    onReset: () -> Unit,
    onSwitchServe: () -> Unit,
    onStartNewGame: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (gameSettings.showTitle) {
                        Text("Table Tennis", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.MatchHistoryScreen.route) }) {
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
            BottomBarActions(
                isFinished = gameState.isFinished,
                showSwitchServe = gameSettings.markServe,
                onReset = onReset,
                onSwitchServe = onSwitchServe,
                onStartNewGame = onStartNewGame
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (gameSettings.showSets) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (gameSettings.showNames) {
                            Text(
                                text = gameState.player1.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(
                            text = gameState.player1SetsWon.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Text(text = ":", style = MaterialTheme.typography.headlineSmall)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (gameSettings.showNames) {
                            Text(
                                text = gameState.player2.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(
                            text = gameState.player2SetsWon.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            PlayerScoreCard(
                playerName = gameState.player1.name,
                score = gameState.player1.score,
                isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player1.id,
                isFinished = gameState.isFinished,
                showPlayerName = gameSettings.showNames,
                onIncrement = { onIncrement(gameState.player1.id) },
                onDecrement = { onDecrement(gameState.player1.id) },
                modifier = Modifier.weight(1f)
            )

            if (gameSettings.markDeuce && gameState.isDeuce) {
                DeuceIndicator()
            }

            PlayerScoreCard(
                playerName = gameState.player2.name,
                score = gameState.player2.score,
                isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player2.id,
                isFinished = gameState.isFinished,
                showPlayerName = gameSettings.showNames,
                onIncrement = { onIncrement(gameState.player2.id) },
                onDecrement = { onDecrement(gameState.player2.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScoreScreenLandscape(
    navController: NavController,
    gameState: GameState,
    gameSettings: GameSettings,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
    onReset: () -> Unit,
    onSwitchServe: () -> Unit,
    onStartNewGame: () -> Unit,
) {
    Scaffold { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerScoreCard(
                playerName = gameState.player1.name,
                score = gameState.player1.score,
                isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player1.id,
                isFinished = gameState.isFinished,
                showPlayerName = gameSettings.showNames,
                onIncrement = { onIncrement(gameState.player1.id) },
                onDecrement = { onDecrement(gameState.player1.id) },
                modifier = Modifier.weight(1f)
            )

            CentralControls(
                modifier = Modifier.padding(horizontal = 4.dp),
                navController = navController,
                gameState = gameState,
                gameSettings = gameSettings,
                onReset = onReset,
                onSwitchServe = onSwitchServe,
                onStartNewGame = onStartNewGame
            )

            PlayerScoreCard(
                playerName = gameState.player2.name,
                score = gameState.player2.score,
                isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player2.id,
                isFinished = gameState.isFinished,
                showPlayerName = gameSettings.showNames,
                onIncrement = { onIncrement(gameState.player2.id) },
                onDecrement = { onDecrement(gameState.player2.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CentralControls(
    modifier: Modifier = Modifier,
    navController: NavController,
    gameState: GameState,
    gameSettings: GameSettings,
    onReset: () -> Unit,
    onSwitchServe: () -> Unit,
    onStartNewGame: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { navController.navigate(Screen.MatchHistoryScreen.route) }) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
            IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (gameSettings.showSets) {
                SetsIndicator(
                    player1Name = "P1",
                    player2Name = "P2",
                    player1Sets = gameState.player1SetsWon,
                    player2Sets = gameState.player2SetsWon
                )
            }
            if (gameSettings.markDeuce && gameState.isDeuce) {
                DeuceIndicator()
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (gameState.isFinished) {
                Button(
                    onClick = onStartNewGame,
                    shape = MaterialTheme.shapes.extraLarge,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Game")
                }
            } else {
                OutlinedButton(
                    onClick = onReset,
                    shape = MaterialTheme.shapes.extraLarge,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
                if (gameSettings.markServe) {
                    OutlinedButton(
                        onClick = onSwitchServe,
                        shape = MaterialTheme.shapes.extraLarge,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch")
                    }
                }
            }
        }
    }
}

@Composable
fun SetsIndicator(
    player1Name: String,
    player2Name: String,
    player1Sets: Int,
    player2Sets: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = player1Name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = player1Sets.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Text(text = ":", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.outline)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = player2Name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = player2Sets.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
    showPlayerName: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onIncrement,
        enabled = !isFinished,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isServing) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isServing) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showPlayerName) {
                        Text(
                            playerName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isServing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.SportsTennis,
                            contentDescription = "Serving",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = score.toString(),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 96.sp,
                    letterSpacing = (-4).sp
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
    showSwitchServe: Boolean,
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
                .padding(16.dp)
                .navigationBarsPadding(),
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
                if (showSwitchServe) {
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
}

@Composable
fun DeuceIndicator(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
    ) {
        Text(
            text = "DEUCE",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

private class FakeSettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(GameSettings())
    override fun getSettings(): StateFlow<GameSettings> = _settings.asStateFlow()
    override suspend fun saveSettings(settings: GameSettings) {
        _settings.value = settings
    }
}

private class FakeMatchRepository : MatchRepository {
    private val matches = mutableListOf<Match>()
    override fun getMatchList(): Flow<List<Match>> = flowOf(matches)
    override suspend fun saveMatch(match: Match) {
        matches.add(match)
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenPreview() {
    val previewViewModel = createPreviewViewModel()
    val navController = rememberNavController()

    ScoreCountTheme {
        ScoreScreen(viewModel = previewViewModel, navController = navController)
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape")
@Composable
fun ScoreScreenLandscapePreview() {
    val previewViewModel = createPreviewViewModel()
    val navController = rememberNavController()

    ScoreCountTheme {
        ScoreScreen(viewModel = previewViewModel, navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenFinishedPreview() {
    val previewViewModel = createPreviewViewModel(finished = true)
    val navController = rememberNavController()

    ScoreCountTheme {
        ScoreScreen(viewModel = previewViewModel, navController = navController)
    }
}

private fun createPreviewViewModel(finished: Boolean = false): ScoreViewModel {
    val dummyP1 = Player(id = 1, name = "Player 1", score = if (finished) 0 else 10)
    val dummyP2 = Player(id = 2, name = "Player 2", score = if (finished) 0 else 10)
    val previewGameState = GameState(
        player1 = dummyP1,
        player2 = dummyP2,
        servingPlayerId = 1,
        player1SetsWon = 3,
        player2SetsWon = 1,
        isDeuce = !finished,
        isFinished = finished
    )
    val fakeScoreRepo = FakeScoreRepositoryPreview(initialState = previewGameState)
    val fakeSettingsRepo = FakeSettingsRepository()
    val fakeMatchRepo = FakeMatchRepository()

    return ScoreViewModel(
        scoreRepository = fakeScoreRepo,
        incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepo, fakeSettingsRepo),
        decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepo),
        manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepo),
        resetGameUseCase = ResetGameUseCase(fakeScoreRepo, fakeSettingsRepo),
        saveMatchUseCase = SaveMatchUseCase(fakeMatchRepo),
        settingsRepository = fakeSettingsRepo
    )
}

class FakeScoreRepositoryPreview(initialState: GameState) : ScoreRepository {
    private val _gameState = MutableStateFlow(initialState)

    override fun getGameState(): StateFlow<GameState> = _gameState.asStateFlow()

    override suspend fun updateGameState(newState: GameState) {
        _gameState.value = newState
    }
}
