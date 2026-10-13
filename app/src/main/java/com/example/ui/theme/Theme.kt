package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberVoid,
    primaryContainer = CyberGlassSurfaceElevated,
    onPrimaryContainer = NeonCyan,
    secondary = VividLime,
    onSecondary = CyberVoid,
    secondaryContainer = CyberGlassSurface,
    onSecondaryContainer = VividLime,
    tertiary = HotAmber,
    onTertiary = CyberVoid,
    background = CyberVoid,
    onBackground = TextPrimary,
    surface = CyberGlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberGlassSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = CyberGlassBorder,
    outlineVariant = CyberGlassBorderHighlight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CyberVoid.toArgb()
                window.navigationBarColor = CyberVoid.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
