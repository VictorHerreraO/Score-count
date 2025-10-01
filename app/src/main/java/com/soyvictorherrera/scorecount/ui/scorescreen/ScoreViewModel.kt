package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val getGameStateUseCase: GetGameStateUseCase,
    private val incrementScoreUseCase: IncrementScoreUseCase,
    private val decrementScoreUseCase: DecrementScoreUseCase,
    private val switchServeUseCase: SwitchServeUseCase,
    private val resetGameUseCase: ResetGameUseCase,
    private val undoLastActionUseCase: UndoLastActionUseCase
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val history: MutableList<GameState> = mutableListOf()

    init {
        viewModelScope.launch {
            val initialState = getGameStateUseCase.execute().first() // Get initial state from repository
            _gameState.value = initialState
            history.add(initialState.copy()) // Save initial state for undo
        }
    }

    private fun updateStateAndStoreHistory(newState: GameState) {
        val current = _gameState.value
        if (current != null && current != newState) { // Store only if state actually changed
            history.add(current.copy()) // Store current state before update
            if (history.size > 10) { // Limit history size
                history.removeAt(0)
            }
        }
        _gameState.value = newState
    }

    fun incrementScore(playerId: Int) {
        viewModelScope.launch {
            incrementScoreUseCase.execute(playerId)
            // State will be updated via observing getGameStateUseCase
            val updatedState = getGameStateUseCase.execute().first()
            updateStateAndStoreHistory(updatedState)
        }
    }

    fun decrementScore(playerId: Int) {
        viewModelScope.launch {
            decrementScoreUseCase.execute(playerId)
            val updatedState = getGameStateUseCase.execute().first()
            updateStateAndStoreHistory(updatedState)
        }
    }

    fun switchServe() {
        viewModelScope.launch {
            switchServeUseCase.execute()
            val updatedState = getGameStateUseCase.execute().first()
            updateStateAndStoreHistory(updatedState)
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            resetGameUseCase.execute()
            val updatedState = getGameStateUseCase.execute().first()
            // When resetting, clear history and add the new initial state
            history.clear()
            history.add(updatedState.copy())
            _gameState.value = updatedState // Explicitly set, as updateStateAndStoreHistory might not if state is same due to race
        }
    }

    fun undoLastAction() {
        viewModelScope.launch {
            if (history.size > 1) {
                history.removeLast() // Remove current state from history tail
                val previousState = history.last().copy()
                _gameState.value = previousState // Restore previous state
                // No need to call repository's undo, as we are managing history here
                // However, if repository's undo is the source of truth for undo, then call it:
                // undoLastActionUseCase.execute()
                // val restoredState = getGameStateUseCase.execute().first()
                // _gameState.value = restoredState
                // And history might need to be rebuilt or aligned with repo's state changes.
                // For now, ViewModel manages undo history independently after an action.
            }
        }
    }
}
