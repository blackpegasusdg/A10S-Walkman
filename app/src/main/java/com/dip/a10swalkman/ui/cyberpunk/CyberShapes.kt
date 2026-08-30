package com.dip.a10swalkman.ui.cyberpunk

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object CyberShapes {
    val ChamferCard = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp, topEnd = 3.dp, bottomStart = 3.dp)
    val ChamferButton = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp, topEnd = 2.dp, bottomStart = 2.dp)
    val ChamferChip = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp, topEnd = 0.dp, bottomStart = 0.dp)
    val ChamferArtwork = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp)
    val HexagonShape = CutCornerShape(percent = 20)
    val PillRounded = RoundedCornerShape(percent = 50)
}

/**
 * Draws high-tech corner brackets ⌜ ⌝ ⌞ ⌟ on the composable
 */
fun Modifier.drawCyberBrackets(
    bracketColor: Color = CyberColors.NeonCyan,
    bracketLength: Dp = 10.dp,
    strokeWidth: Dp = 1.5.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val len = bracketLength.toPx()
        val stroke = strokeWidth.toPx()
        val w = size.width
        val h = size.height

        // Top-Left ⌜
        drawLine(bracketColor, Offset(0f, 0f), Offset(len, 0f), stroke)
        drawLine(bracketColor, Offset(0f, 0f), Offset(0f, len), stroke)

        // Top-Right ⌝
        drawLine(bracketColor, Offset(w - len, 0f), Offset(w, 0f), stroke)
        drawLine(bracketColor, Offset(w, 0f), Offset(w, len), stroke)

        // Bottom-Left ⌞
        drawLine(bracketColor, Offset(0f, h - len), Offset(0f, h), stroke)
        drawLine(bracketColor, Offset(0f, h), Offset(len, h), stroke)

        // Bottom-Right ⌟
        drawLine(bracketColor, Offset(w - len, h), Offset(w, h), stroke)
        drawLine(bracketColor, Offset(w, h - len), Offset(w, h), stroke)
    }
)

/**
 * Draws high-tech scanlines across the background
 */
fun Modifier.drawCyberGrid(
    gridColor: Color = Color(0x0800F0FF),
    step: Dp = 24.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val stepPx = step.toPx()
        val w = size.width
        val h = size.height

        var y = 0f
        while (y < h) {
            drawLine(gridColor, Offset(0f, y), Offset(w, y), 1f)
            y += stepPx
        }

        var x = 0f
        while (x < w) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, h), 1f)
            x += stepPx
        }
    }
)
