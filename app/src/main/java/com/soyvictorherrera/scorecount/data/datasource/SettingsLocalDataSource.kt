package com.soyvictorherrera.scorecount.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private object PreferencesKeys {
            val KEY_SHOW_TITLE = booleanPreferencesKey("show_title")
            val KEY_SHOW_NAMES = booleanPreferencesKey("show_names")
            val KEY_SHOW_SETS = booleanPreferencesKey("show_sets")
            val KEY_MARK_SERVE = booleanPreferencesKey("mark_serve")
            val KEY_MARK_DEUCE = booleanPreferencesKey("mark_deuce")
            val KEY_POINTS_TO_WIN_SET = intPreferencesKey("points_to_win_set")
            val KEY_WIN_BY_TWO = booleanPreferencesKey("win_by_two")
            val KEY_NUMBER_OF_SETS = intPreferencesKey("number_of_sets")
            val KEY_SERVE_ROTATION_AFTER_POINTS = intPreferencesKey("serve_rotation_after_points")
            val KEY_SERVE_CHANGE_AFTER_DEUCE = intPreferencesKey("serve_change_after_deuce")
            val KEY_WINNER_SERVES_NEXT_GAME = booleanPreferencesKey("winner_serves_next_game")
            val KEY_KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        }

        val settings: StateFlow<GameSettings> =
            dataStore.data
                .map { preferences ->
                    val default = GameSettings()
                    GameSettings(
                        showTitle = preferences[PreferencesKeys.KEY_SHOW_TITLE] ?: default.showTitle,
                        showNames = preferences[PreferencesKeys.KEY_SHOW_NAMES] ?: default.showNames,
                        showSets = preferences[PreferencesKeys.KEY_SHOW_SETS] ?: default.showSets,
                        markServe = preferences[PreferencesKeys.KEY_MARK_SERVE] ?: default.markServe,
                        markDeuce = preferences[PreferencesKeys.KEY_MARK_DEUCE] ?: default.markDeuce,
                        pointsToWinSet = preferences[PreferencesKeys.KEY_POINTS_TO_WIN_SET] ?: default.pointsToWinSet,
                        winByTwo = preferences[PreferencesKeys.KEY_WIN_BY_TWO] ?: default.winByTwo,
                        numberOfSets = preferences[PreferencesKeys.KEY_NUMBER_OF_SETS] ?: default.numberOfSets,
                        serveRotationAfterPoints =
                            preferences[PreferencesKeys.KEY_SERVE_ROTATION_AFTER_POINTS]
                                ?: default.serveRotationAfterPoints,
                        serveChangeAfterDeuce =
                            preferences[PreferencesKeys.KEY_SERVE_CHANGE_AFTER_DEUCE] ?: default.serveChangeAfterDeuce,
                        winnerServesNextGame =
                            preferences[PreferencesKeys.KEY_WINNER_SERVES_NEXT_GAME] ?: default.winnerServesNextGame,
                        keepScreenOn = preferences[PreferencesKeys.KEY_KEEP_SCREEN_ON] ?: default.keepScreenOn
                    )
                }.stateIn(
                    scope = scope,
                    started = SharingStarted.Eagerly,
                    initialValue = GameSettings()
                )

        suspend fun saveSettings(settings: GameSettings) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.KEY_SHOW_TITLE] = settings.showTitle
                preferences[PreferencesKeys.KEY_SHOW_NAMES] = settings.showNames
                preferences[PreferencesKeys.KEY_SHOW_SETS] = settings.showSets
                preferences[PreferencesKeys.KEY_MARK_SERVE] = settings.markServe
                preferences[PreferencesKeys.KEY_MARK_DEUCE] = settings.markDeuce
                preferences[PreferencesKeys.KEY_POINTS_TO_WIN_SET] = settings.pointsToWinSet
                preferences[PreferencesKeys.KEY_WIN_BY_TWO] = settings.winByTwo
                preferences[PreferencesKeys.KEY_NUMBER_OF_SETS] = settings.numberOfSets
                preferences[PreferencesKeys.KEY_SERVE_ROTATION_AFTER_POINTS] = settings.serveRotationAfterPoints
                preferences[PreferencesKeys.KEY_SERVE_CHANGE_AFTER_DEUCE] = settings.serveChangeAfterDeuce
                preferences[PreferencesKeys.KEY_WINNER_SERVES_NEXT_GAME] = settings.winnerServesNextGame
                preferences[PreferencesKeys.KEY_KEEP_SCREEN_ON] = settings.keepScreenOn
            }
        }
    }
