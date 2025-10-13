package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarActions(
    isFinished: Boolean,
    showSwitchServe: Boolean,
    onReset: () -> Unit,
    onSwitchServe: () -> Unit,
    onStartNewGame: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isFinished) {
                Button(
                    onClick = onStartNewGame,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(com.soyvictorherrera.scorecount.R.string.action_start_new_game))
                }
            } else {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(com.soyvictorherrera.scorecount.R.string.action_reset))
                }
                if (showSwitchServe) {
                    OutlinedButton(
                        onClick = onSwitchServe,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Serve")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(com.soyvictorherrera.scorecount.R.string.action_switch_serve))
                    }
                }
            }
        }
    }
}
