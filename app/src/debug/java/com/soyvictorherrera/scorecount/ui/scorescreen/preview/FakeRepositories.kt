package com.soyvictorherrera.scorecount.ui.scorescreen.preview

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeSettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(GameSettings())

    override fun getSettings(): StateFlow<GameSettings> = _settings.asStateFlow()

    override suspend fun saveSettings(settings: GameSettings) {
        _settings.value = settings
    }
}

class FakeMatchRepository : MatchRepository {
    private val matches = mutableListOf<Match>()

    override fun getMatchList(): Flow<List<Match>> = flowOf(matches)

    override suspend fun saveMatch(match: Match) {
        matches.add(match)
    }
}

class FakeScoreRepository(
    initialState: GameState
) : ScoreRepository {
    private val _gameState = MutableStateFlow(initialState)
    private val _hasUndoHistory = MutableStateFlow(false)

    override fun getGameState(): StateFlow<GameState> = _gameState.asStateFlow()

    override suspend fun updateGameState(newState: GameState) {
        _gameState.value = newState
    }

    override suspend fun undoLastChange() {
        // No-op for preview
    }

    override fun hasUndoHistory(): StateFlow<Boolean> = _hasUndoHistory.asStateFlow()

    override suspend fun clearHistory() {
        // No-op for preview
    }
}
