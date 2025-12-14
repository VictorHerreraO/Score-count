package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreenDefaults

@Composable
fun HorizontalMatchScore(
    gameState: GameState,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 16.dp,
    dividerContent: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = horizontalSpacing),
        modifier = modifier,
    ) {
        val isPlayer1Serving = gameState.servingPlayerId == gameState.player1.id
        val player1ScoreAlpha =
            with(ScoreScreenDefaults) {
                if (isPlayer1Serving) ALPHA_PRIMARY else ALPHA_SECONDARY
            }
        val player2ScoreAlpha =
            with(ScoreScreenDefaults) {
                if (!isPlayer1Serving) ALPHA_PRIMARY else ALPHA_SECONDARY
            }

        Text(
            text = gameState.player1SetsWon.toString(),
            style =
                MaterialTheme.typography.displaySmall.copy(
                    fontWeight = if (isPlayer1Serving) FontWeight.Bold else FontWeight.Normal
                ),
            modifier = Modifier.alpha(alpha = player1ScoreAlpha)
        )

        dividerContent()

        Text(
            text = gameState.player2SetsWon.toString(),
            style =
                MaterialTheme.typography.displaySmall.copy(
                    fontWeight = if (!isPlayer1Serving) FontWeight.Bold else FontWeight.Normal
                ),
            modifier = Modifier.alpha(alpha = player2ScoreAlpha)
        )
    }
}
