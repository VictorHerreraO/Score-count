package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MatchScoreTopAppBar(
    gameState: GameState,
    gameSettings: GameSettings
) {
    CenterAlignedTopAppBar(
        title = {
            HorizontalMatchScore(
                gameState = gameState,
                horizontalSpacing = 8.dp,
                dividerContent = {
                    if (gameSettings.showNames) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.player_p1),
                                style = MaterialTheme.typography.labelMedium
                            )

                            GameSets(
                                matchNumber = gameState.currentSet,
                                numberOfSets = gameSettings.numberOfSets
                            )

                            Text(
                                text = stringResource(R.string.player_p2),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    } else {
                        GameSets(
                            matchNumber = gameState.currentSet,
                            numberOfSets = gameSettings.numberOfSets
                        )
                    }
                }
            )
        }
    )
}
