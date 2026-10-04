package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ArcadeIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = ArcadeIndigoDark,
    onPrimaryContainer = Color.White,
    secondary = ArcadeGold,
    onSecondary = Color(0xFF1E1B4B),
    secondaryContainer = ArcadeGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = ArcadeCyanLight,
    onTertiary = Color(0xFF0C4A6E),
    background = ArcadeDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = ArcadeDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = ArcadeDarkCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = ArcadePink,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ArcadeIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = ArcadeIndigoDark,
    secondary = ArcadeAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = ArcadeGoldDark,
    tertiary = ArcadeCyan,
    onTertiary = Color.White,
    background = ArcadeLightBg,
    onBackground = Color(0xFF0F172A),
    surface = ArcadeLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = ArcadeLightCard,
    onSurfaceVariant = Color(0xFF475569),
    error = ArcadePink,
    onError = Color.White
)

@Composable
fun KidsArcadeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep arcade branded colors by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
