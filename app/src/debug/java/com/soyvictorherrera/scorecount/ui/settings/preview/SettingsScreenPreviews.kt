package com.soyvictorherrera.scorecount.ui.settings.preview

import android.annotation.SuppressLint
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.ui.settings.SettingsScreen
import com.soyvictorherrera.scorecount.ui.settings.SettingsViewModel
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ScoreCountTheme {
        Surface {
            SettingsScreen(
                onNavigateBack = {},
                settingsViewModel =
                    SettingsViewModel(
                        settingsRepository =
                            object : SettingsRepository {
                                private val settingsFlow = MutableStateFlow(GameSettings())

                                override fun getSettings(): StateFlow<GameSettings> = settingsFlow.asStateFlow()

                                override suspend fun saveSettings(settings: GameSettings) {
                                    settingsFlow.value = settings
                                }
                            },
                        dispatcher = Dispatchers.Unconfined
                    )
            )
        }
    }
}
