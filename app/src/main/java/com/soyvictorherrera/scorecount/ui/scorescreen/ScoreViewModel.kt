package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    getGameStateUseCase: GetGameStateUseCase,
    private val incrementScoreUseCase: IncrementScoreUseCase,
    private val decrementScoreUseCase: DecrementScoreUseCase,
    private val manualSwitchServeUseCase: ManualSwitchServeUseCase, // Updated Usecase name
    private val resetGameUseCase: ResetGameUseCase,
    private val undoLastActionUseCase: UndoLastActionUseCase,
    private val saveMatchUseCase: SaveMatchUseCase,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _gameSettings = MutableStateFlow<GameSettings?>(null)
    val gameSettings: StateFlow<GameSettings?> = _gameSettings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _gameSettings.value = settings
            }
        }
        viewModelScope.launch {
            getGameStateUseCase.execute().collect { currentGameState ->
                val previousState = _gameState.value
                _gameState.value = currentGameState

                if (currentGameState.isFinished && previousState?.isFinished == false) {
                    saveMatch(currentGameState)
                }
            }
        }
    }

    fun incrementScore(playerId: Int) {
        viewModelScope.launch {
            incrementScoreUseCase.execute(playerId)
        }
    }

    fun decrementScore(playerId: Int) {
        viewModelScope.launch {
            decrementScoreUseCase.execute(playerId)
        }
    }

    fun manualSwitchServe() {
        viewModelScope.launch {
            manualSwitchServeUseCase.execute()
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            resetGameUseCase.execute()
        }
    }

    fun undoLastAction() {
        viewModelScope.launch {
            undoLastActionUseCase.execute()
        }
    }

    private fun saveMatch(gameState: GameState) {
        viewModelScope.launch {
            val match = Match(
                id = "",
                players = gameState.player1 to gameState.player2,
                score = gameState.player1SetsWon to gameState.player2SetsWon,
                date = System.currentTimeMillis()
            )
            saveMatchUseCase.execute(match)
        }
    }
}
