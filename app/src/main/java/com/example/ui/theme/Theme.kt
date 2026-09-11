package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MovloDarkColorScheme = darkColorScheme(
    primary = MovloGold,
    onPrimary = Color.Black,
    primaryContainer = MovloGoldDark,
    onPrimaryContainer = MovloGoldBright,
    secondary = MovloCyan,
    onSecondary = Color.Black,
    tertiary = MovloEmerald,
    onTertiary = Color.Black,
    background = MovloDarkBg,
    onBackground = MovloTextPrimary,
    surface = MovloDarkSurface,
    onSurface = MovloTextPrimary,
    surfaceVariant = MovloDarkCard,
    onSurfaceVariant = MovloTextSecondary,
    outline = MovloBorder,
    error = MovloRed
)

// For cinematic streaming apps, dark theme is the native primary aesthetic
private val MovloLightColorScheme = MovloDarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand cinema aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MovloDarkColorScheme,
        typography = Typography,
        content = content
    )
}
