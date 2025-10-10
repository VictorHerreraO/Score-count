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
    private val scoreRepository: com.soyvictorherrera.scorecount.domain.repository.ScoreRepository,
    private val incrementScoreUseCase: IncrementScoreUseCase,
    private val decrementScoreUseCase: DecrementScoreUseCase,
    private val manualSwitchServeUseCase: ManualSwitchServeUseCase,
    private val resetGameUseCase: ResetGameUseCase,
    private val saveMatchUseCase: SaveMatchUseCase,
    settingsRepository: SettingsRepository
) : ViewModel() {

    // Directly expose StateFlows from repositories - no need for intermediate copying
    val gameState: StateFlow<GameState> = scoreRepository.getGameState()
    val gameSettings: StateFlow<GameSettings> = settingsRepository.getSettings()

    init {
        // Monitor game state changes to auto-save matches
        viewModelScope.launch {
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
            val currentState = gameState.value
            val winnerId = if (currentState.player1SetsWon > currentState.player2SetsWon) {
                currentState.player1.id
            } else {
                currentState.player2.id
            }
            resetGameUseCase.execute(winnerId)
        }
    }

    private fun saveMatch(gameState: GameState) {
        viewModelScope.launch {
            val match = Match(
                id = "",
                playerOneName = gameState.player1.name,
                playerTwoName = gameState.player2.name,
                playerOneScore = gameState.player1SetsWon,
                playerTwoScore = gameState.player2SetsWon,
                date = System.currentTimeMillis()
            )
            saveMatchUseCase.execute(match)
        }
    }
}
