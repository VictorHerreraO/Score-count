package com.soyvictorherrera.scorecount

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soyvictorherrera.scorecount.ui.Screen
import com.soyvictorherrera.scorecount.ui.main.MainViewModel
import com.soyvictorherrera.scorecount.ui.matchhistory.MatchHistoryScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreViewModel
import com.soyvictorherrera.scorecount.ui.settings.SettingsScreen
import com.soyvictorherrera.scorecount.ui.settings.SettingsViewModel
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Mutable reference to ScoreViewModel, set by ScoreScreen composable
    private var scoreViewModel: ScoreViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val settings by mainViewModel.settings.collectAsState()

            LaunchedEffect(settings.keepScreenOn) {
                val keepScreenOn = settings.keepScreenOn
                if (keepScreenOn) {
                    window.apply {
                        addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        attributes =
                            attributes.also {
                                it.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                            }
                    }
                } else {
                    window.apply {
                        clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        attributes =
                            attributes.also {
                                it.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                            }
                    }
                }
            }

            ScoreCountTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.ScoreScreen.route) {
                    composable(Screen.ScoreScreen.route) {
                        val viewModel: ScoreViewModel = hiltViewModel()

                        // Store reference to ViewModel for S Pen key event handling
                        DisposableEffect(viewModel) {
                            scoreViewModel = viewModel
                            onDispose { scoreViewModel = null }
                        }

                        ScoreScreen(
                            viewModel = viewModel,
                            onNavigateToHistory = { navController.navigate(Screen.MatchHistoryScreen.route) },
                            onNavigateToSettings = { navController.navigate(Screen.SettingsScreen.route) }
                        )
                    }
                    composable(Screen.SettingsScreen.route) {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            settingsViewModel = settingsViewModel
                        )
                    }
                    composable(Screen.MatchHistoryScreen.route) {
                        MatchHistoryScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?
    ): Boolean {
        // Only handle S Pen events when ScoreViewModel is available (i.e., on ScoreScreen)
        val viewModel = scoreViewModel
        if (viewModel != null) {
            return when (keyCode) {
                KeyEvent.KEYCODE_PAGE_DOWN -> {
                    // Single click: increment player 1
                    viewModel.incrementScore(playerId = 1)
                    Log.d("MainActivity", "S Pen single click: Player 1 score incremented")
                    true
                }
                KeyEvent.KEYCODE_PAGE_UP -> {
                    // Double click: increment player 2
                    viewModel.incrementScore(playerId = 2)
                    Log.d("MainActivity", "S Pen double click: Player 2 score incremented")
                    true
                }
                else -> super.onKeyDown(keyCode, event)
            }
        }

        // Not on ScoreScreen or no ViewModel - pass through
        return super.onKeyDown(keyCode, event)
    }
}
