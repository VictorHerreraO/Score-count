package com.soyvictorherrera.scorecount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soyvictorherrera.scorecount.ui.Screen
import com.soyvictorherrera.scorecount.ui.matchhistory.MatchHistoryScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreViewModel
import com.soyvictorherrera.scorecount.ui.settings.SettingsScreen
import com.soyvictorherrera.scorecount.ui.settings.SettingsViewModel
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // ViewModels are obtained via hiltViewModel() at the composable level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoreCountTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.ScoreScreen.route) {
                    composable(Screen.ScoreScreen.route) {
                        val scoreViewModel: ScoreViewModel = hiltViewModel()
                        ScoreScreen(
                            viewModel = scoreViewModel,
                            navController = navController
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
