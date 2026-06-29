package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = AppleBlue,
    secondary = ApplePurple,
    tertiary = AppleGreen,
    background = MacOSDarkBg,
    surface = MacOSDarkCard,
    onBackground = Color(0xFFE2E8F0), // Slate-200
    onSurface = Color(0xFFCBD5E1) // Slate-300
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AppleBlue,
    secondary = ApplePurple,
    tertiary = AppleGreen,
    background = MacOSLightBg,
    surface = MacOSLightCard,
    onBackground = Color(0xFF1E293B), // Slate-800
    onSurface = Color(0xFF334155) // Slate-700
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  accentColor: Color = AppleBlue,
  content: @Composable () -> Unit,
) {
  val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val colorScheme = baseScheme.copy(
    primary = accentColor,
    onPrimary = Color.White
  )

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

