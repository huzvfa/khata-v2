package com.khata.finance.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Emerald,
    onPrimary = PureBlack,
    secondary = Gold,
    onSecondary = PureBlack,
    background = PureBlack,
    onBackground = TextOnDark,
    surface = CardBlack,
    onSurface = TextOnDark,
    surfaceVariant = CardBlack2,
    onSurfaceVariant = TextMutedDark,
    outline = DarkBorder,
    error = ExpenseRed
)

private val LightColors = lightColorScheme(
    primary = EmeraldDeep,
    onPrimary = LightCard,
    secondary = Gold,
    onSecondary = TextOnLight,
    background = LightBg,
    onBackground = TextOnLight,
    surface = LightCard,
    onSurface = TextOnLight,
    surfaceVariant = LightBg,
    onSurfaceVariant = TextMutedLight,
    outline = LightBorder,
    error = ExpenseRed
)

@Composable
fun KhataTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
