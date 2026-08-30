package com.dip.a10swalkman.ui.swiss

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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.AlbumArt
import com.dip.a10swalkman.MusicFile
import kotlin.random.Random

// ============================================================================
// SWISS CARD
// ============================================================================

@Composable
fun SwissCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SwissColors.Surface,
    borderColor: Color = SwissColors.Hairline,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(2.dp)
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = SwissColors.White),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(clickModifier)
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), shape)
            .padding(16.dp)
    ) {
        content()
    }
}

// ============================================================================
// SWISS AUDIO INDICATOR (MINIMAL 3-BAR PULSE)
// ============================================================================

@Composable
fun SwissAudioIndicator(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = SwissColors.White
) {
    val transition = rememberInfiniteTransition(label = "swiss_audio")
    val bar1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Box(
        modifier = modifier
            .drawBehind {
                val maxHeight = size.height
                val barW = 2.5.dp.toPx()
                val gap = 2.dp.toPx()

                val heights = if (isPlaying) {
                    listOf(bar1 * maxHeight, bar2 * maxHeight, bar3 * maxHeight)
                } else {
                    listOf(3.dp.toPx(), 3.dp.toPx(), 3.dp.toPx())
                }

                heights.forEachIndexed { i, h ->
                    val x = i * (barW + gap)
                    val y = maxHeight - h.coerceAtLeast(2.dp.toPx())
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(barW, h.coerceAtLeast(2.dp.toPx())),
                        cornerRadius = CornerRadius(1f, 1f)
                    )
                }
            }
    )
}

// ============================================================================
// SWISS BUTTON
// ============================================================================

@Composable
fun SwissButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(2.dp)
    val bg = if (isPrimary) SwissColors.White else SwissColors.Surface
    val fg = if (isPrimary) SwissColors.Black else SwissColors.White
    val border = if (isPrimary) Color.Transparent else SwissColors.HairlineLight

    Box(
        modifier = modifier
            .clip(shape)
            .background(if (enabled) bg else SwissColors.SurfaceElevated)
            .border(BorderStroke(1.dp, border), shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = fg),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
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
                    tint = if (enabled) fg else SwissColors.GrayMid,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                color = if (enabled) fg else SwissColors.GrayMid,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ============================================================================
// SWISS ICON BUTTON
// ============================================================================

@Composable
fun SwissIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = SwissColors.White,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color? = null,
    size: Dp = 40.dp,
    contentDescription: String? = null
) {
    val shape = RoundedCornerShape(2.dp)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .then(if (borderColor != null) Modifier.border(BorderStroke(1.dp, borderColor), shape) else Modifier)
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
// SWISS BADGE
// ============================================================================

@Composable
fun SwissBadge(
    text: String,
    modifier: Modifier = Modifier,
    hasAccentDot: Boolean = false
) {
    Row(
        modifier = modifier
            .border(BorderStroke(1.dp, SwissColors.Hairline), RoundedCornerShape(2.dp))
            .background(SwissColors.Surface)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasAccentDot) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(SwissColors.Accent)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text.uppercase(),
            color = SwissColors.GrayLight,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

// ============================================================================
// SWISS WAVEFORM SCRUBBER (INTERACTIVE AUDIO WAVEFORM PROGRESS BAR)
// ============================================================================

@Composable
fun SwissWaveformScrubber(
    currentPosition: Int,
    duration: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
    seed: Long = 42L,
    isPlaying: Boolean = false,
    barCount: Int = 46,
    height: Dp = 44.dp
) {
    val actualProgress = if (duration > 0) {
        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val displayProgress = if (isDragging) dragProgress else actualProgress
    val displayPosition = (displayProgress * duration).toInt()

    // Deterministic waveform profile for the track
    val rawWaveform = remember(seed, barCount) {
        val random = Random(seed xor 0x5DEECE66DL)
        val arr = FloatArray(barCount)
        for (i in 0 until barCount) {
            val normalizedPos = i.toFloat() / (barCount - 1).coerceAtLeast(1)
            // Multi-frequency harmonic envelope + natural audio dynamic variation
            val envelope = 0.35f +
                    0.45f * kotlin.math.sin(normalizedPos * Math.PI.toFloat()) +
                    0.20f * kotlin.math.sin(normalizedPos * 3 * Math.PI.toFloat()).coerceAtLeast(0f)
            val noise = 0.4f + 0.6f * random.nextFloat()
            arr[i] = (envelope * noise).coerceIn(0.12f, 1.0f)
        }
        arr
    }

    // Micro-motion breathing when playing
    val transition = rememberInfiniteTransition(label = "waveform_pulse")
    val livePulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livePulse"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Waveform Canvas with Tap & Drag Seeking
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .pointerInput(duration) {
                    detectTapGestures { offset ->
                        if (duration > 0) {
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            onSeek((fraction * duration).toInt())
                        }
                    }
                }
                .pointerInput(duration) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            if (duration > 0) {
                                onSeek((dragProgress * duration).toInt())
                            }
                            isDragging = false
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newX = (dragProgress * size.width) + dragAmount.x
                            dragProgress = (newX / size.width).coerceIn(0f, 1f)
                        }
                    )
                }
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val centerY = h / 2f
                    val gap = 2.dp.toPx()
                    val totalBars = rawWaveform.size
                    val barWidth = ((w - ((totalBars - 1) * gap)) / totalBars).coerceAtLeast(1.5f)

                    for (i in 0 until totalBars) {
                        val barFraction = i.toFloat() / (totalBars - 1).coerceAtLeast(1)
                        val isPlayed = barFraction <= displayProgress

                        // Base bar height with subtle active pulsation
                        val baseAmplitude = rawWaveform[i]
                        val dynamicFactor = if (isPlaying && isPlayed) {
                            val phase = (i % 3)
                            when (phase) {
                                0 -> livePulse
                                1 -> (2f - livePulse)
                                else -> 1f
                            }
                        } else 1f

                        val barHeight = (h * 0.85f * baseAmplitude * dynamicFactor).coerceIn(4.dp.toPx(), h)
                        val x = i * (barWidth + gap)
                        val y = centerY - (barHeight / 2f)

                        val barColor = when {
                            isPlayed -> SwissColors.White
                            else -> SwissColors.HairlineLight
                        }

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                        )
                    }

                    // Playhead cursor indicator
                    val playheadX = (displayProgress * w).coerceIn(0f, w)
                    drawLine(
                        color = if (isDragging) SwissColors.Accent else SwissColors.White,
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, h),
                        strokeWidth = if (isDragging) 2.dp.toPx() else 1.dp.toPx()
                    )

                    // Small indicator top dot when dragging
                    if (isDragging) {
                        drawCircle(
                            color = SwissColors.Accent,
                            radius = 3.5.dp.toPx(),
                            center = Offset(playheadX, 0f)
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Timecode Readout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatSwissTime(displayPosition),
                color = if (isDragging) SwissColors.Accent else SwissColors.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Text(
                text = formatSwissTime(duration),
                color = SwissColors.GrayMid,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ============================================================================
// SWISS SCRUBBER (ALIAS)
// ============================================================================

@Composable
fun SwissScrubber(
    currentPosition: Int,
    duration: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
    songId: Long = 42L,
    isPlaying: Boolean = false
) {
    SwissWaveformScrubber(
        currentPosition = currentPosition,
        duration = duration,
        onSeek = onSeek,
        seed = songId,
        isPlaying = isPlaying,
        modifier = modifier
    )
}

// ============================================================================
// SWISS ALBUM ART
// ============================================================================

@Composable
fun SwissAlbumArt(
    song: MusicFile?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: RoundedCornerShape = RoundedCornerShape(2.dp)
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val targetSizePx = remember(size, density) {
        with(density) { size.roundToPx() }
    }

    var bitmap by remember(song?.id) {
        mutableStateOf(if (song != null) AlbumArt.getCached(song.id) else null)
    }

    LaunchedEffect(song?.id, targetSizePx) {
        if (song != null && bitmap == null && !AlbumArt.isKnownMissing(song.id)) {
            val loaded = AlbumArt.loadArtworkAsync(context, song, targetSizePx)
            bitmap = loaded
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(SwissColors.SurfaceElevated)
            .border(BorderStroke(1.dp, SwissColors.Hairline), shape),
        contentAlignment = Alignment.Center
    ) {
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = song?.title ?: "Album Art",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = SwissColors.GrayMid,
                modifier = Modifier.size((size.value * 0.4f).dp)
            )
        }
    }
}

// ============================================================================
// SWISS HEADER
// ============================================================================

@Composable
fun SwissHeader(
    indexNumber: String = "01",
    title: String = "WALKMAN",
    subtitle: String? = null,
    queueCount: Int = 0,
    onSearchClick: () -> Unit,
    onQueueClick: () -> Unit,
    onLogoutClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SwissColors.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBackClick != null) {
                    SwissIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBackClick,
                        size = 32.dp,
                        tint = SwissColors.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$indexNumber / ",
                        color = SwissColors.Accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title.uppercase(),
                        color = SwissColors.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                SwissIconButton(
                    icon = Icons.Default.Search,
                    onClick = onSearchClick,
                    size = 36.dp,
                    tint = SwissColors.White
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box {
                    SwissIconButton(
                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                        onClick = onQueueClick,
                        size = 36.dp,
                        tint = if (queueCount > 0) SwissColors.White else SwissColors.GrayMid
                    )
                    if (queueCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SwissColors.Accent)
                        )
                    }
                }

                if (onLogoutClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    SwissIconButton(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        onClick = onLogoutClick,
                        size = 36.dp,
                        tint = SwissColors.GrayLight,
                        contentDescription = "Sign Out"
                    )
                }
            }
        }

        // Hairline Structural Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SwissColors.Hairline)
        )
    }
}

// ============================================================================
// SWISS SUBPAGE HEADER
// ============================================================================

@Composable
fun SwissSubPageHeader(
    indexNumber: String = "INDEX",
    title: String,
    itemCount: Int? = null,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SwissColors.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwissIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack,
                size = 32.dp,
                tint = SwissColors.White
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$indexNumber / ",
                        color = SwissColors.Accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title.uppercase(),
                        color = SwissColors.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                if (itemCount != null) {
                    Text(
                        text = "$itemCount ENTRIES",
                        color = SwissColors.GrayMid,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SwissColors.Hairline)
        )
    }
}

// ============================================================================
// SWISS SEARCH BAR
// ============================================================================

@Composable
fun SwissSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "SEARCH REPOSITORY..."
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, SwissColors.Hairline), RoundedCornerShape(2.dp))
            .background(SwissColors.Surface)
            .padding(horizontal = 14.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SwissColors.GrayLight,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = SwissColors.GrayMid,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = SwissColors.White,
                    unfocusedTextColor = SwissColors.White,
                    cursorColor = SwissColors.White
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
                        tint = SwissColors.GrayLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// SWISS EMPTY VIEW
// ============================================================================

@Composable
fun SwissEmptyView(
    title: String = "EMPTY ARCHIVE",
    subtitle: String = "NO AUDIO FILES DETECTED IN STORAGE",
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
        Text(
            text = "[ — ]",
            color = SwissColors.GrayMid,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title.uppercase(),
            color = SwissColors.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            color = SwissColors.GrayMid,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        if (actionButtonText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            SwissButton(
                text = actionButtonText,
                onClick = onAction
            )
        }
    }
}

// ============================================================================
// SWISS TIME FORMATTER
// ============================================================================

fun formatSwissTime(milliseconds: Int): String {
    val totalSeconds = (milliseconds / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
