package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.di.DefaultDispatcher
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.ScoreUseCases
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
        private val scoreUseCases: ScoreUseCases,
        settingsRepository: SettingsRepository,
        @DefaultDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {
        // Directly expose StateFlows from repositories - no need for intermediate copying
        val gameState: StateFlow<GameState> = scoreRepository.getGameState()
        val gameSettings: StateFlow<GameSettings> = settingsRepository.getSettings()
        val hasUndoHistory: StateFlow<Boolean> = scoreRepository.hasUndoHistory()

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
                scoreUseCases.increment(playerId)
            }
        }

        fun decrementScore(playerId: Int) {
            viewModelScope.launch {
                scoreUseCases.decrement(playerId)
            }
        }

        fun manualSwitchServe() {
            viewModelScope.launch {
                scoreUseCases.switchServe()
            }
        }

        fun resetGame() {
            viewModelScope.launch {
                scoreUseCases.reset()
            }
        }

        fun undoLastChange() {
            viewModelScope.launch {
                scoreUseCases.undo()
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
                scoreUseCases.saveMatch(match)
            }
        }
    }
