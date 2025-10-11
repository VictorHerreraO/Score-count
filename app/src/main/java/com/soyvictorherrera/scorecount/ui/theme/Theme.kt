package com.soyvictorherrera.scorecount.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkBackground,
    surfaceVariant = DarkCardServing,
    surfaceContainer = DarkCardNormal,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    onPrimary = Color.White,
    surfaceContainerHighest = DarkButtonBg
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,
    surface = LightBackground,
    surfaceVariant = LightCardServing,
    surfaceContainer = LightCardNormal,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    onPrimary = Color.White,
    surfaceContainerHighest = LightButtonBg
)

@Composable
fun ScoreCountTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android S+ an higher.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // window.statusBarColor = colorScheme.primary.toArgb() // Removed
            // WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme // Removed
            // Modern approach: Control system bar appearance (light/dark icons)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme // Light icons for dark theme, Dark icons for light theme
            // For coloring, typically done by setting window.statusBarColor = Color.Transparent.toArgb()
            // and then using WindowCompat.setDecorFitsSystemWindows(window, false) and drawing behind.
            // Or, rely on the XML theme's statusBarColor (e.g. via colorSurface).
            // If the XML theme is basic, this might result in a default system status bar.
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}