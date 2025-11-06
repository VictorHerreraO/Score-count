package com.soyvictorherrera.scorecount.ui.evaluation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.model.Set

/**
 * Compose Charts Demo
 *
 * NOTE: This is a placeholder. The tehras/charts library was rejected during evaluation
 * due to being abandoned (last updated January 2023). Using unmaintained alpha software
 * is an unacceptable production risk.
 *
 * Key rejection reasons:
 * - Abandoned project (2+ years without updates)
 * - Alpha version (never reached stable)
 * - Security risk from unmaintained dependencies
 * - No bug fixes or Compose compatibility updates
 *
 * See docs/match_details_screen/CHART_LIBRARY_DECISION.md for full evaluation.
 */
@Composable
fun ComposeChartsDemo(
    set: Set,
    player1: Player,
    player2: Player,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Compose Charts - Set ${set.setNumber} (Rejected)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Score: ${set.finalScore.player1Score} - ${set.finalScore.player2Score}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text =
                    "Compose Charts was rejected due to being abandoned (2+ years). " +
                        "Using unmaintained alpha libraries creates unacceptable security and " +
                        "compatibility risks. See CHART_LIBRARY_DECISION.md for details.",
                style = MaterialTheme.typography.bodySmall,
                modifier =
                    Modifier
                        .height(240.dp)
                        .padding(8.dp)
            )
        }
    }
}
