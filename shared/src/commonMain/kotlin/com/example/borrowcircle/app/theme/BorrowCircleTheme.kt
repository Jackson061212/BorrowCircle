package com.example.borrowcircle.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BorrowCircleColors {
    val Accent = Color(0xFF0B6B61)
    val AccentSoft = Color(0xFFE1F1EE)
    val Background = Color(0xFFF8F7F2)
    val Surface = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF1B1D1C)
    val TextSecondary = Color(0xFF636A67)
    val Border = Color(0xFFD9DEDB)
}

private val BorrowCircleLightColors = lightColorScheme(
    primary = BorrowCircleColors.Accent,
    onPrimary = Color.White,
    primaryContainer = BorrowCircleColors.AccentSoft,
    onPrimaryContainer = Color(0xFF06433D),
    background = BorrowCircleColors.Background,
    onBackground = BorrowCircleColors.TextPrimary,
    surface = BorrowCircleColors.Surface,
    onSurface = BorrowCircleColors.TextPrimary,
    surfaceVariant = Color(0xFFF0F1EE),
    onSurfaceVariant = BorrowCircleColors.TextSecondary,
    outline = BorrowCircleColors.Border,
)

@Composable
fun BorrowCircleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BorrowCircleLightColors,
        typography = Typography(),
        content = content,
    )
}
