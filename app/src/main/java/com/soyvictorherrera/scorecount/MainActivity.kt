package com.soyvictorherrera.scorecount

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val settings by mainViewModel.settings.collectAsState()

            LaunchedEffect(settings.keepScreenOn) {
                val keepScreenOn = settings.keepScreenOn
                if (keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            ScoreCountTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.ScoreScreen.route) {
                    composable(Screen.ScoreScreen.route) {
                        val scoreViewModel: ScoreViewModel = hiltViewModel()
                        ScoreScreen(
                            viewModel = scoreViewModel,
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
}
