package com.soyvictorherrera.scorecount.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.soyvictorherrera.scorecount.GameStateProto

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val Context.gameStateDataStore: DataStore<GameStateProto> by dataStore(
    fileName = "game_state.pb",
    serializer = GameStateSerializer
)
