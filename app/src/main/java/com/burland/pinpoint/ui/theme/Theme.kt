package com.burland.pinpoint.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// We strongly prefer Dark Theme for "Privacy" aesthetic
private val DarkColorScheme = darkColorScheme(
    primary = PrivacyOrange,
    secondary = PrivacyOrangeDim,
    tertiary = LightGrey,
    background = Black,
    surface = DarkGrey,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onBackground = White,
    onSurface = White,
)

// Fallback Light Theme (though we intend to enforce dark)
private val LightColorScheme = lightColorScheme(
    primary = PrivacyOrange,
    secondary = PrivacyOrangeDim,
    tertiary = LightGrey
)

@Composable
fun PinpointTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // We default to FALSE to maintain our custom Privacy Orange identity
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Force Dark Theme preference
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
