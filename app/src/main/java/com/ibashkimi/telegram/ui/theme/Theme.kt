package com.ibashkimi.telegram.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TelegramTheme(isDark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isDark) telegramDarkColors else telegramLightColors,
        content = content
    )
}

private val telegramLightColors = lightColors(
    primary = Color(0xff527da3),
    primaryVariant = Color(0xff426482),
    onPrimary = Color.White,
    secondary = Color(0xff65a9e0),
    secondaryVariant = Color(0xff65a9e0),
    onSecondary = Color.White
)

private val telegramDarkColors = darkColors(
    primary = Color(0xff212d3b),
    primaryVariant = Color(0xff1a242f),
    onPrimary = Color.White,
    secondary = Color(0xff5fa3de),
    onSecondary = Color.White,
    background = Color(0xff1d2733)
)