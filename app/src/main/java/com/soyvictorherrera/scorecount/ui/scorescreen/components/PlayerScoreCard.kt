package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soyvictorherrera.scorecount.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    isServing: Boolean,
    isFinished: Boolean,
    showPlayerName: Boolean,
    callbacks: PlayerScoreCardCallbacks,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = callbacks.onIncrement,
        enabled = !isFinished,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isServing) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = if (isServing) 4.dp else 1.dp
            )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showPlayerName) {
                    Text(
                        playerName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isServing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.SportsTennis,
                        contentDescription = stringResource(id = R.string.cd_serving),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = score.toString(),
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 96.sp,
                letterSpacing = (-4).sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
