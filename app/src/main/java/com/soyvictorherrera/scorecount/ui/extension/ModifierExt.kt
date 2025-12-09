package com.soyvictorherrera.scorecount.ui.extension

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha

/**
 * A [Modifier] that applies a shimmering effect by animating the alpha value of the content.
 * The alpha value oscillates between a minimum value and 1.0f in a repeating reverse loop.
 *
 * @param minAlpha The minimum alpha value for the shimmer effect. Defaults to `0.25f`.
 * @return A [Modifier] that applies the shimmering animation.
 */
fun Modifier.shimmering(minAlpha: Float = .25f): Modifier =
    composed {
        val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

        val value by infiniteTransition.animateFloat(
            initialValue = minAlpha,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
            label = "shimmerAlpha"
        )

        alpha(value)
    }
