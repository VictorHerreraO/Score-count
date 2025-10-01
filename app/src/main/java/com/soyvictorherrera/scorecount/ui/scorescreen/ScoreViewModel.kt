package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val getGameStateUseCase: GetGameStateUseCase,
    private val incrementScoreUseCase: IncrementScoreUseCase,
    private val decrementScoreUseCase: DecrementScoreUseCase,
    private val manualSwitchServeUseCase: ManualSwitchServeUseCase, // Updated Usecase name
    private val resetGameUseCase: ResetGameUseCase,
    private val undoLastActionUseCase: UndoLastActionUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _gameSettings = MutableStateFlow<GameSettings?>(null)
    val gameSettings: StateFlow<GameSettings?> = _gameSettings.asStateFlow()

    // Using a simpler history for undo: just the immediate previous state.
    // LocalScoreDataSource now manages its own previous state for undo.
    // This ViewModel history might be redundant or could be used for more complex scenarios if needed.
    private var lastKnownGoodState: GameState? = null

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _gameSettings.value = settings
                // If settings change, we might need to re-evaluate current game state or re-fetch.
                // For now, assume settings are mostly applied on new actions or resets.
            }
        }
        viewModelScope.launch {
            getGameStateUseCase.execute().collect { currentGameState ->
                _gameState.value = currentGameState
                lastKnownGoodState = currentGameState // Keep track for simple undo
            }
        }
    }

    // The updateStateAndStoreHistory is not strictly needed if LocalScoreDataSource handles undo history.
    // The getGameStateUseCase.execute().first() pattern after every action might also be simplified
    // if getGameStateUseCase().collect {} is the single source of truth for _gameState.

    fun incrementScore(playerId: Int) {
        viewModelScope.launch {
            incrementScoreUseCase.execute(playerId)
            // GameState is updated via the collect block in init
        }
    }

    fun decrementScore(playerId: Int) {
        viewModelScope.launch {
            decrementScoreUseCase.execute(playerId)
            // GameState is updated via the collect block in init
        }
    }

    // Renamed to reflect it's a manual action from UI
    fun manualSwitchServe() {
        viewModelScope.launch {
            manualSwitchServeUseCase.execute() // Calls the renamed use case
            // GameState is updated via the collect block in init
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            resetGameUseCase.execute()
            // GameState is updated via the collect block in init
        }
    }

    fun undoLastAction() {
        viewModelScope.launch {
            undoLastActionUseCase.execute()
            // GameState is updated via the collect block in init
            // The LocalScoreDataSource handles the undo logic.
            // The ViewModel's history might be used for UI-specific undo if different from data source.
        }
    }
}
