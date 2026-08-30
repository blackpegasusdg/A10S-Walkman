package com.dip.a10swalkman.ui.cyberpunk

import android.graphics.Bitmap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.AlbumArt
import com.dip.a10swalkman.MusicFile

// ============================================================================
// CYBER CARD
// ============================================================================

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberColors.CardBorder,
    glowColor: Color = CyberColors.NeonCyan,
    showBrackets: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = CyberShapes.ChamferCard
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = glowColor),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(clickModifier)
            .clip(shape)
            .background(CyberColors.CardGradient)
            .border(1.dp, borderColor, shape)
            .then(if (showBrackets) Modifier.drawCyberBrackets(bracketColor = glowColor, bracketLength = 8.dp) else Modifier)
            .padding(14.dp)
    ) {
        content()
    }
}

// ============================================================================
// CYBER AUDIO VISUALIZER
// ============================================================================

@Composable
fun CyberAudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16,
    barColorStart: Color = CyberColors.NeonCyan,
    barColorEnd: Color = CyberColors.NeonPink
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_vis")

    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase2"
    )
    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase3"
    )

    Box(
        modifier = modifier
            .drawBehind {
                val totalWidth = size.width
                val maxHeight = size.height
                val barWidth = (totalWidth / (barCount * 1.5f)).coerceAtLeast(2f)
                val spacing = (totalWidth - (barWidth * barCount)) / (barCount - 1).coerceAtLeast(1)

                val gradient = Brush.verticalGradient(
                    colors = listOf(barColorEnd, barColorStart),
                    startY = 0f,
                    endY = maxHeight
                )

                for (i in 0 until barCount) {
                    val progress = if (isPlaying) {
                        val factor = when (i % 4) {
                            0 -> phase1
                            1 -> phase2
                            2 -> phase3
                            else -> ((phase1 + phase2) / 2f)
                        }
                        val centerWeight = 1f - kotlin.math.abs((i - (barCount / 2f)) / (barCount / 2f)) * 0.4f
                        (factor * centerWeight).coerceIn(0.1f, 1.0f)
                    } else {
                        0.08f + (i % 3) * 0.04f
                    }

                    val barHeight = (maxHeight * progress).coerceAtLeast(3f)
                    val x = i * (barWidth + spacing)
                    val y = maxHeight - barHeight

                    drawRoundRect(
                        brush = gradient,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }
            }
    )
}

// ============================================================================
// CYBER BUTTON
// ============================================================================

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color = CyberColors.NeonCyan,
    enabled: Boolean = true
) {
    val shape = CyberShapes.ChamferButton
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (enabled) CyberColors.SurfaceElevated else CyberColors.Surface)
            .border(1.dp, if (enabled) accentColor else CyberColors.TextMuted, shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .drawCyberBrackets(bracketColor = if (enabled) accentColor else Color.Transparent, bracketLength = 6.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) accentColor else CyberColors.TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                color = if (enabled) CyberColors.TextPrimary else CyberColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
        }
    }
}

// ============================================================================
// CYBER ICON BUTTON
// ============================================================================

@Composable
fun CyberIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = CyberColors.NeonCyan,
    backgroundColor: Color = CyberColors.SurfaceElevated,
    borderColor: Color = CyberColors.CardBorder,
    size: Dp = 42.dp,
    contentDescription: String? = null
) {
    val shape = CyberShapes.ChamferButton
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = tint),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size((size.value * 0.55f).dp)
        )
    }
}

// ============================================================================
// CYBER BADGE / CHIP
// ============================================================================

@Composable
fun CyberBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CyberColors.NeonCyan,
    backgroundColor: Color = CyberColors.Surface
) {
    Box(
        modifier = modifier
            .clip(CyberShapes.ChamferChip)
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.6f), CyberShapes.ChamferChip)
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}

// ============================================================================
// CYBER SCRUBBER
// ============================================================================

@Composable
fun CyberScrubber(
    currentPosition: Int,
    duration: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (duration > 0) {
        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = progress,
            onValueChange = { newProgress ->
                val newPosition = (newProgress * duration).toInt()
                onSeek(newPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = CyberColors.NeonCyan,
                activeTrackColor = CyberColors.NeonCyan,
                inactiveTrackColor = CyberColors.SurfaceHighlight
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatCyberTime(currentPosition),
                color = CyberColors.NeonCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Text(
                text = "// ${formatCyberTime(duration)}",
                color = CyberColors.TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

// ============================================================================
// CYBER ALBUM ART
// ============================================================================

@Composable
fun CyberAlbumArt(
    song: MusicFile?,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    isPlaying: Boolean = false,
    glowColor: Color = CyberColors.NeonCyan
) {
    val context = LocalContext.current
    val bitmap = remember(song?.id) {
        if (song != null) {
            try {
                AlbumArt.getArtwork(context, song)
            } catch (_: Exception) {
                null
            }
        } else null
    }

    val shape = CyberShapes.ChamferArtwork

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(CyberColors.Surface)
            .border(1.5.dp, if (isPlaying) glowColor else CyberColors.CardBorder, shape)
            .drawCyberBrackets(bracketColor = if (isPlaying) glowColor else CyberColors.TextMuted, bracketLength = (size.value * 0.15f).dp),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = song?.title ?: "Album Artwork",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(CyberColors.SurfaceElevated, CyberColors.DarkBg)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isPlaying) glowColor else CyberColors.TextMuted,
                    modifier = Modifier.size((size.value * 0.45f).dp)
                )
            }
        }
    }
}

// ============================================================================
// CYBER HEADER
// ============================================================================

@Composable
fun CyberHeader(
    title: String = "A10S // WALKMAN",
    subtitle: String = "CORE AUDIO ENGINE // v2.0",
    queueCount: Int = 0,
    onSearchClick: () -> Unit,
    onQueueClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.DarkBg)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(CyberColors.NeonCyan, Color.Transparent, CyberColors.NeonPink)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    CyberIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBackClick,
                        size = 36.dp,
                        tint = CyberColors.NeonCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberColors.NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            color = CyberColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        text = subtitle,
                        color = CyberColors.NeonCyanDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberIconButton(
                    icon = Icons.Default.Search,
                    onClick = onSearchClick,
                    size = 36.dp,
                    tint = CyberColors.NeonCyan
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    CyberIconButton(
                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                        onClick = onQueueClick,
                        size = 36.dp,
                        tint = if (queueCount > 0) CyberColors.NeonPink else CyberColors.TextSecondary
                    )

                    if (queueCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 2.dp, end = 2.dp)
                                .clip(CyberShapes.ChamferChip)
                                .background(CyberColors.NeonPink)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$queueCount",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// CYBER SUBPAGE HEADER
// ============================================================================

@Composable
fun CyberSubPageHeader(
    title: String,
    itemCount: Int? = null,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.DarkBg)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(CyberColors.NeonCyan, Color.Transparent)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CyberIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            onClick = onBack,
            size = 36.dp,
            tint = CyberColors.NeonCyan
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title.uppercase(),
                color = CyberColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            if (itemCount != null) {
                Text(
                    text = "SYS_ENTRIES // $itemCount TRACKS LOADED",
                    color = CyberColors.NeonCyanDim,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ============================================================================
// CYBER SEARCH BAR
// ============================================================================

@Composable
fun CyberSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "SCAN REPOSITORY (TRACK / ARTIST / ALBUM)..."
) {
    val shape = CyberShapes.ChamferCard
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CyberColors.SurfaceElevated)
            .border(1.dp, CyberColors.NeonCyan, shape)
            .drawCyberBrackets(CyberColors.NeonCyan, bracketLength = 6.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = CyberColors.NeonCyan,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = CyberColors.TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = CyberColors.TextPrimary,
                    unfocusedTextColor = CyberColors.TextPrimary,
                    cursorColor = CyberColors.NeonCyan
                )
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = CyberColors.NeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// CYBER EMPTY VIEW
// ============================================================================

@Composable
fun CyberEmptyView(
    title: String = "NO AUDIO TRANSMISSIONS FOUND",
    subtitle: String = "CHECK PERMISSIONS OR LOAD TRACKS INTO STORAGE",
    actionButtonText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CyberShapes.ChamferCard)
                .background(CyberColors.SurfaceElevated)
                .border(1.dp, CyberColors.NeonCyan.copy(alpha = 0.5f), CyberShapes.ChamferCard)
                .drawCyberBrackets(CyberColors.NeonCyan, bracketLength = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = CyberColors.NeonCyanDim,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            color = CyberColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            color = CyberColors.TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        if (actionButtonText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            CyberButton(
                text = actionButtonText,
                onClick = onAction
            )
        }
    }
}

// ============================================================================
// TIME FORMATTER
// ============================================================================

fun formatCyberTime(milliseconds: Int): String {
    val totalSeconds = (milliseconds / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
