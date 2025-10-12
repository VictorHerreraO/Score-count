package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.di.DefaultDispatcher
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel
    @Inject
    constructor(
        private val scoreRepository: com.soyvictorherrera.scorecount.domain.repository.ScoreRepository,
        private val incrementScoreUseCase: IncrementScoreUseCase,
        private val decrementScoreUseCase: DecrementScoreUseCase,
        private val manualSwitchServeUseCase: ManualSwitchServeUseCase,
        private val resetGameUseCase: ResetGameUseCase,
        private val saveMatchUseCase: SaveMatchUseCase,
        settingsRepository: SettingsRepository,
        @DefaultDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {
        // Directly expose StateFlows from repositories - no need for intermediate copying
        val gameState: StateFlow<GameState> = scoreRepository.getGameState()
        val gameSettings: StateFlow<GameSettings> = settingsRepository.getSettings()

        init {
            // Monitor game state changes to auto-save matches
            viewModelScope.launch(dispatcher) {
                var previousState: GameState? = null
                gameState.collect { currentGameState ->
                    if (currentGameState.isFinished && previousState?.isFinished == false) {
                        saveMatch(currentGameState)
                    }
                    previousState = currentGameState
                }
            }
        }

        fun incrementScore(playerId: Int) {
            viewModelScope.launch {
                incrementScoreUseCase(playerId)
            }
        }

        fun decrementScore(playerId: Int) {
            viewModelScope.launch {
                decrementScoreUseCase(playerId)
            }
        }

        fun manualSwitchServe() {
            viewModelScope.launch {
                manualSwitchServeUseCase()
            }
        }

        fun resetGame() {
            viewModelScope.launch {
                resetGameUseCase()
            }
        }

        private fun saveMatch(gameState: GameState) {
            viewModelScope.launch {
                val match =
                    Match(
                        id = "",
                        playerOneName = gameState.player1.name,
                        playerTwoName = gameState.player2.name,
                        playerOneScore = gameState.player1SetsWon,
                        playerTwoScore = gameState.player2SetsWon,
                        date = System.currentTimeMillis()
                    )
                saveMatchUseCase(match)
            }
        }
    }
