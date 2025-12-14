package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.R

@Composable
fun GameSets(
    matchNumber: Int,
    numberOfSets: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Max)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.game_match_number, matchNumber),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .weight(1f)
                        .alpha(0.85f),
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = stringResource(R.string.game_best_of, numberOfSets),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
