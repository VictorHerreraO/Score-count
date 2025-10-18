package com.soyvictorherrera.scorecount.ui.scorescreen.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.usecase.DecrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.IncrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ManualSwitchServeUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ResetGameUseCase
import com.soyvictorherrera.scorecount.domain.usecase.SaveMatchUseCase
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreViewModel
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.Dispatchers

@Preview(showBackground = true)
@Composable
fun ScoreScreenPreview() {
    val previewViewModel = createPreviewViewModel()

    ScoreCountTheme {
        ScoreScreen(
            viewModel = previewViewModel,
            onNavigateToHistory = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape")
@Composable
fun ScoreScreenLandscapePreview() {
    val previewViewModel = createPreviewViewModel()

    ScoreCountTheme {
        ScoreScreen(
            viewModel = previewViewModel,
            onNavigateToHistory = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenFinishedPreview() {
    val previewViewModel = createPreviewViewModel(finished = true)

    ScoreCountTheme {
        ScoreScreen(
            viewModel = previewViewModel,
            onNavigateToHistory = {},
            onNavigateToSettings = {}
        )
    }
}

private fun createPreviewViewModel(finished: Boolean = false): ScoreViewModel {
    val dummyP1 = Player(id = 1, name = "Player 1", score = if (finished) 0 else 10)
    val dummyP2 = Player(id = 2, name = "Player 2", score = if (finished) 0 else 10)
    val previewGameState =
        GameState(
            player1 = dummyP1,
            player2 = dummyP2,
            servingPlayerId = 1,
            player1SetsWon = 3,
            player2SetsWon = 1,
            isDeuce = !finished,
            isFinished = finished
        )
    val fakeScoreRepo = FakeScoreRepository(initialState = previewGameState)
    val fakeSettingsRepo = FakeSettingsRepository()
    val fakeMatchRepo = FakeMatchRepository()

    return ScoreViewModel(
        scoreRepository = fakeScoreRepo,
        incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepo, fakeSettingsRepo),
        decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepo, fakeSettingsRepo),
        manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepo),
        resetGameUseCase = ResetGameUseCase(fakeScoreRepo, fakeSettingsRepo),
        saveMatchUseCase = SaveMatchUseCase(fakeMatchRepo),
        settingsRepository = fakeSettingsRepo,
        dispatcher = Dispatchers.Unconfined
    )
}
