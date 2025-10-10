package com.soyvictorherrera.scorecount.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.soyvictorherrera.scorecount.GameStateProto

val Context.gameStateDataStore: DataStore<GameStateProto> by dataStore(
    fileName = "game_state.pb",
    serializer = GameStateSerializer
)
