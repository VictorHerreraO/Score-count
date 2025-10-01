package com.soyvictorherrera.scorecount.data.datasource

import android.content.SharedPreferences
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val KEY_SHOW_TITLE = "show_title"
        const val KEY_SHOW_NAMES = "show_names"
        const val KEY_SHOW_SETS = "show_sets"
        const val KEY_MARK_SERVE = "mark_serve"
        const val KEY_MARK_DEUCE = "mark_deuce"
        const val KEY_SHOW_PREVIOUS_SETS = "show_previous_sets"
        const val KEY_POINTS_TO_WIN_SET = "points_to_win_set"
        const val KEY_WIN_BY_TWO = "win_by_two" // Not in UI yet, but in model
        const val KEY_NUMBER_OF_SETS = "number_of_sets"
        const val KEY_SERVE_ROTATION_AFTER_POINTS = "serve_rotation_after_points"
        const val KEY_SERVE_CHANGE_AFTER_DEUCE = "serve_change_after_deuce" // Not in UI yet, but in model
        const val KEY_WINNER_SERVES_NEXT_GAME = "winner_serves_next_game"
    }

    fun getSettings(): Flow<GameSettings> = flow {
        val default = GameSettings()
        val settings = GameSettings(
            showTitle = sharedPreferences.getBoolean(KEY_SHOW_TITLE, default.showTitle),
            showNames = sharedPreferences.getBoolean(KEY_SHOW_NAMES, default.showNames),
            showSets = sharedPreferences.getBoolean(KEY_SHOW_SETS, default.showSets),
            markServe = sharedPreferences.getBoolean(KEY_MARK_SERVE, default.markServe),
            markDeuce = sharedPreferences.getBoolean(KEY_MARK_DEUCE, default.markDeuce),
            showPreviousSets = sharedPreferences.getBoolean(KEY_SHOW_PREVIOUS_SETS, default.showPreviousSets),
            pointsToWinSet = sharedPreferences.getInt(KEY_POINTS_TO_WIN_SET, default.pointsToWinSet),
            winByTwo = sharedPreferences.getBoolean(KEY_WIN_BY_TWO, default.winByTwo),
            numberOfSets = sharedPreferences.getInt(KEY_NUMBER_OF_SETS, default.numberOfSets),
            serveRotationAfterPoints = sharedPreferences.getInt(KEY_SERVE_ROTATION_AFTER_POINTS, default.serveRotationAfterPoints),
            serveChangeAfterDeuce = sharedPreferences.getInt(KEY_SERVE_CHANGE_AFTER_DEUCE, default.serveChangeAfterDeuce),
            winnerServesNextGame = sharedPreferences.getBoolean(KEY_WINNER_SERVES_NEXT_GAME, default.winnerServesNextGame)
        )
        emit(settings)
    }.flowOn(Dispatchers.IO)

    suspend fun saveSettings(settings: GameSettings) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_SHOW_TITLE, settings.showTitle)
            putBoolean(KEY_SHOW_NAMES, settings.showNames)
            putBoolean(KEY_SHOW_SETS, settings.showSets)
            putBoolean(KEY_MARK_SERVE, settings.markServe)
            putBoolean(KEY_MARK_DEUCE, settings.markDeuce)
            putBoolean(KEY_SHOW_PREVIOUS_SETS, settings.showPreviousSets)
            putInt(KEY_POINTS_TO_WIN_SET, settings.pointsToWinSet)
            putBoolean(KEY_WIN_BY_TWO, settings.winByTwo)
            putInt(KEY_NUMBER_OF_SETS, settings.numberOfSets)
            putInt(KEY_SERVE_ROTATION_AFTER_POINTS, settings.serveRotationAfterPoints)
            putInt(KEY_SERVE_CHANGE_AFTER_DEUCE, settings.serveChangeAfterDeuce)
            putBoolean(KEY_WINNER_SERVES_NEXT_GAME, settings.winnerServesNextGame)
            apply()
        }
    }
}
