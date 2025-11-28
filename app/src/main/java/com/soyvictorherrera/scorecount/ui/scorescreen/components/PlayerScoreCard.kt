package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soyvictorherrera.scorecount.R

@Composable
fun PlayerScoreCard(
    state: PlayerScoreCardState,
    showPlayerName: Boolean,
    callbacks: PlayerScoreCardCallbacks,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = callbacks.onIncrement,
        enabled = !state.isFinished,
        shape = MaterialTheme.shapes.extraLarge,
        modifier =
            modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha =
                        when {
                            state.isFinished -> 0.75f
                            state.isServing -> DefaultAlpha
                            else -> 0.85f
                        }
                },
        border = playerScoreCardBorder(showBorder = state.isServing or state.isFinished),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showPlayerName) {
                Text(
                    state.playerName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                Text(
                    text = state.score.toString(),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 96.sp,
                    letterSpacing = (-4).sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (state.isWinning) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = stringResource(id = R.string.cd_winning),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

data class PlayerScoreCardState(
    val playerName: String,
    val score: Int,
    val isServing: Boolean,
    val isFinished: Boolean,
    val isWinning: Boolean
)

@Composable
private fun playerScoreCardBorder(showBorder: Boolean): BorderStroke {
    val color =
        if (showBorder) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Unspecified
        }
    return remember(color) { BorderStroke(width = 2.dp, color = color) }
}
