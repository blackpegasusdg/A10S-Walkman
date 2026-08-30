package com.dip.a10swalkman.ui.cyberpunk

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object CyberColors {
    // Backgrounds
    val Void = Color(0xFF050811)
    val DarkBg = Color(0xFF080D1A)
    val Surface = Color(0xFF0E1626)
    val SurfaceElevated = Color(0xFF152238)
    val SurfaceHighlight = Color(0xFF1A2A45)
    val Card = Color(0xFF0F1B2F)
    val CardBorder = Color(0xFF1B2F4E)

    // Neons
    val NeonCyan = Color(0xFF00F0FF)
    val NeonCyanDim = Color(0xFF00A3AD)
    val NeonCyanGlow = Color(0x3300F0FF)

    val NeonPink = Color(0xFFFF0055)
    val NeonPinkDim = Color(0xFFB3003B)
    val NeonPinkGlow = Color(0x33FF0055)

    val NeonYellow = Color(0xFFFFE600)
    val NeonYellowDim = Color(0xFFB3A100)

    val NeonGreen = Color(0xFF39FF14)
    val NeonGreenDim = Color(0xFF229E0B)

    val NeonPurple = Color(0xFFB026FF)
    val NeonPurpleDim = Color(0xFF7515AF)

    // Text & Indicators
    val TextPrimary = Color(0xFFF0F6FC)
    val TextSecondary = Color(0xFF8B9CB3)
    val TextMuted = Color(0xFF48586E)
    val TextCyan = Color(0xFF66F4FF)

    // HUD & Grids
    val HudGrid = Color(0x0F00F0FF)
    val HudBracket = Color(0xFF00F0FF)
    val HudScanner = Color(0x2200F0FF)

    // Gradients
    val CyanPinkGradient = Brush.horizontalGradient(
        listOf(NeonCyan, NeonPink)
    )

    val CyanGlowGradient = Brush.verticalGradient(
        listOf(Color(0x3300F0FF), Color(0x0500F0FF))
    )

    val CardGradient = Brush.verticalGradient(
        listOf(SurfaceElevated, Surface)
    )

    val DarkOverlayGradient = Brush.verticalGradient(
        listOf(Color.Transparent, Color(0xCC050811), Color(0xFF050811))
    )

    val ArtworkGlowGradient = Brush.radialGradient(
        listOf(Color(0x5500F0FF), Color(0x22FF0055), Color.Transparent)
    )
}
