package com.soyvictorherrera.scorecount.ui

sealed class Screen(val route: String) {
    data object ScoreScreen : Screen("score")
    data object SettingsScreen : Screen("settings")
}
