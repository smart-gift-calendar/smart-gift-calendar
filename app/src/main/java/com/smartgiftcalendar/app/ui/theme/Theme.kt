package com.smartgiftcalendar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    secondary = PeachSecondary,
    tertiary = MintTertiary,
    background = BackgroundLight,
    surface = SurfaceLight
)

@Composable
fun SmartGiftCalendarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
