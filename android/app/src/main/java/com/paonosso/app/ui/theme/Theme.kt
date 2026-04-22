package com.paonosso.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Emerald600,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald700,
    secondary = Orange500,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Orange100,
    onSecondaryContainer = Orange700,
    background = androidx.compose.ui.graphics.Color.White,
    onBackground = Gray800,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Gray800,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray500,
    outline = Gray200,
    error = Red500,
)

@Composable
fun PaoNossoTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = PaoNossoTypography,
        shapes = PaoNossoShapes,
        content = content,
    )
}
