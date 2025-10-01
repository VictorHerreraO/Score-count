package com.soyvictorherrera.scorecount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreViewModel
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ScoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoreCountTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }
    }
}
