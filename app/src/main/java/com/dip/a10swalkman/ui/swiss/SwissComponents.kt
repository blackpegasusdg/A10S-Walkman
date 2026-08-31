package com.dip.a10swalkman.ui.swiss

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.AlbumArt
import com.dip.a10swalkman.MusicFile
import kotlin.math.PI
import kotlin.math.sin
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
// SWISS AUDIO EQUALIZER INDICATOR (MINIMAL 4-BAR HARMONIC PULSE)
// ============================================================================

@Composable
fun SwissAudioIndicator(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = SwissColors.White
) {
    val transition = rememberInfiniteTransition(label = "swiss_audio_bars")
    val b1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b1"
    )
    val b2 by transition.animateFloat(
        initialValue = 0.90f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b2"
    )
    val b3 by transition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(340, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b3"
    )
    val b4 by transition.animateFloat(
        initialValue = 0.70f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(460, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b4"
    )

    Box(
        modifier = modifier.drawBehind {
            val maxHeight = size.height
            val totalBars = 4
            val barW = 2.dp.toPx()
            val gap = 2.dp.toPx()

            val heights = if (isPlaying) {
                listOf(b1 * maxHeight, b2 * maxHeight, b3 * maxHeight, b4 * maxHeight)
            } else {
                listOf(2.5.dp.toPx(), 2.5.dp.toPx(), 2.5.dp.toPx(), 2.5.dp.toPx())
            }

            heights.forEachIndexed { i, h ->
                val x = i * (barW + gap)
                val clampedH = h.coerceAtLeast(2.dp.toPx())
                val y = maxHeight - clampedH
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barW, clampedH),
                    cornerRadius = CornerRadius(0.8.dp.toPx(), 0.8.dp.toPx())
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
            .padding(horizontal = 18.dp, vertical = 11.dp),
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
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
            }
            Text(
                text = text.uppercase(),
                color = if (enabled) fg else SwissColors.GrayMid,
                fontSize = 11.sp,
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
            modifier = Modifier.size((size.value * 0.52f).dp)
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
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasAccentDot) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(SwissColors.Accent)
            )
            Spacer(modifier = Modifier.width(5.dp))
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
    songId: Long = 42L,
    seed: Long = songId,
    isPlaying: Boolean = false,
    barCount: Int = 48,
    height: Dp = 50.dp
) {
    val actualProgress = if (duration > 0) {
        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val displayProgress = if (isDragging) dragProgress else actualProgress
    val displayPosition = (displayProgress * duration).toInt()

    // Deterministic realistic multi-harmonic sound envelope
    val rawWaveform = remember(seed, barCount) {
        val random = Random(seed xor 0x5DEECE66DL)
        val arr = FloatArray(barCount)
        for (i in 0 until barCount) {
            val norm = i.toFloat() / (barCount - 1).coerceAtLeast(1)
            // Multi-frequency harmonic envelope + dynamic musical peaks
            val envelope = 0.30f +
                    0.50f * sin(norm * PI.toFloat()) +
                    0.20f * sin(norm * 3 * PI.toFloat()).coerceAtLeast(0f) +
                    0.15f * sin(norm * 7 * PI.toFloat()).coerceAtLeast(0f)
            val noise = 0.35f + 0.65f * random.nextFloat()
            arr[i] = (envelope * noise).coerceIn(0.12f, 1.0f)
        }
        arr
    }

    // Dynamic live frequency reactive bouncing when audio is playing
    val transition = rememberInfiniteTransition(label = "waveform_pulse_anim")
    val pulsePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsePhase"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Waveform Visualizer Canvas with Seek Dragging & Tooltip
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

                        // Base bar height with live frequency bouncing near the playhead
                        val baseAmp = rawWaveform[i]
                        val liveBouncyFactor = if (isPlaying) {
                            val distToPlayhead = kotlin.math.abs(barFraction - displayProgress)
                            if (distToPlayhead < 0.35f) {
                                val proximity = 1f - (distToPlayhead / 0.35f)
                                1f + 0.28f * proximity * sin(pulsePhase + i * 0.8f)
                            } else {
                                1f + 0.08f * sin(pulsePhase + i * 0.4f)
                            }
                        } else 1f

                        val barHeight = (h * 0.82f * baseAmp * liveBouncyFactor).coerceIn(4.dp.toPx(), h)
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
                            cornerRadius = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())
                        )
                    }

                    // Playhead Line Indicator
                    val playheadX = (displayProgress * w).coerceIn(0f, w)

                    // Vertical played line
                    drawLine(
                        color = if (isDragging) SwissColors.Accent else SwissColors.White,
                        start = Offset(playheadX, 0f),
                        end = Offset(playheadX, h),
                        strokeWidth = if (isDragging) 2.dp.toPx() else 1.2.dp.toPx()
                    )

                    // Swiss Red marker dot at top of playhead
                    drawCircle(
                        color = SwissColors.Accent,
                        radius = if (isDragging) 4.dp.toPx() else 2.5.dp.toPx(),
                        center = Offset(playheadX, 3.dp.toPx())
                    )
                }
        ) {
            // Drag Scrub Tooltip Floating Badge
            if (isDragging) {
                val density = LocalDensity.current
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset {
                            IntOffset(
                                x = (dragProgress * 300).toInt().coerceIn(0, 200),
                                y = -28
                            )
                        }
                        .clip(RoundedCornerShape(2.dp))
                        .background(SwissColors.Dark)
                        .border(BorderStroke(1.dp, SwissColors.Accent), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SCRUB // ${formatSwissTime(displayPosition)}",
                        color = SwissColors.Accent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Timecode Readout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(SwissColors.Accent)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(
                    text = formatSwissTime(displayPosition),
                    color = if (isDragging) SwissColors.Accent else SwissColors.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Text(
                text = " ${formatSwissTime(duration)}",
                color = SwissColors.GrayMid,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

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
// SKIPER71 IMAGE REVEAL ANIMATION (CLIP-PATH POLYGON & BRIGHTNESS BLOOM)
// ============================================================================

/**
 * Implements a Skiper71-inspired image reveal:
 * - Dynamic angled clip-path polygon curtain wipe from left-to-right
 * - Exposure and brightness ramp from dark tone into full crisp vibrance
 * - Smooth scale settling (1.06f -> 1.0f)
 * - Authentic Swiss editorial metadata badge overlay
 */
@Composable
fun Skiper71ImageReveal(
    song: MusicFile?,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val targetSizePx = remember(size, density) {
        with(density) { (size * 2).roundToPx().coerceAtLeast(800) }
    }

    var bitmap by remember(song?.id) {
        mutableStateOf(if (song != null) AlbumArt.getCached(song.id, targetSizePx) else null)
    }

    LaunchedEffect(song?.id, targetSizePx) {
        if (song != null && (bitmap == null || (bitmap?.width ?: 0) < targetSizePx) && !AlbumArt.isKnownMissing(song.id)) {
            val loaded = AlbumArt.loadArtworkAsync(context, song, targetSizePx)
            if (loaded != null) {
                bitmap = loaded
            }
        }
    }

    // Skiper71 Reveal Progress Animation (0f -> 1f)
    val revealProgress = remember(song?.id) { Animatable(0f) }

    LaunchedEffect(song?.id) {
        revealProgress.snapTo(0f)
        revealProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 650,
                easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
            )
        )
    }

    val p = revealProgress.value
    val scale = 1.05f - (0.05f * p)

    val shape = RoundedCornerShape(2.dp)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(SwissColors.SurfaceElevated)
            .border(BorderStroke(1.dp, SwissColors.HairlineLight), shape),
        contentAlignment = Alignment.Center
    ) {
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            // Angled Polygon Clip Path Mask for Skiper71 Curtain Effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .drawWithCache {
                        val path = Path().apply {
                            val w = this@drawWithCache.size.width
                            val h = this@drawWithCache.size.height
                            val revealX = w * p
                            val slopeOffset = w * 0.22f * (1f - p)

                            moveTo(0f, 0f)
                            lineTo((revealX + slopeOffset).coerceAtMost(w), 0f)
                            lineTo(revealX.coerceAtMost(w), h)
                            lineTo(0f, h)
                            close()
                        }
                        onDrawWithContent {
                            clipPath(path) {
                                this@onDrawWithContent.drawContent()
                            }
                        }
                    }
            ) {
                Image(
                    bitmap = currentBitmap.asImageBitmap(),
                    contentDescription = song?.title ?: "Album Artwork",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High
                )

                // Brightness & Exposure Wipe Overlay during animation only
                if (p < 0.99f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        SwissColors.Black.copy(alpha = (1f - p) * 0.5f)
                                    )
                                )
                            )
                    )
                }
            }
        } else {
            // Fallback Minimalist Record Placeholder
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = SwissColors.GrayMid,
                    modifier = Modifier.size((size.value * 0.35f).dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SWISS AUDIO DECK",
                    color = SwissColors.GrayDark,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ============================================================================
// SWISS ALBUM ART (WITH EMBEDDED SKIPER71 REVEAL)
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
        with(density) { (size * 2).roundToPx().coerceAtLeast(240) }
    }

    var bitmap by remember(song?.id) {
        mutableStateOf(if (song != null) AlbumArt.getCached(song.id, targetSizePx) else null)
    }

    LaunchedEffect(song?.id, targetSizePx) {
        if (song != null && (bitmap == null || (bitmap?.width ?: 0) < targetSizePx) && !AlbumArt.isKnownMissing(song.id)) {
            val loaded = AlbumArt.loadArtworkAsync(context, song, targetSizePx)
            if (loaded != null) {
                bitmap = loaded
            }
        }
    }

    // Micro-reveal for small thumbnails
    val revealProgress = remember(song?.id) { Animatable(0.2f) }
    LaunchedEffect(song?.id) {
        revealProgress.snapTo(0.2f)
        revealProgress.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
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
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = revealProgress.value
                    },
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High
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
// SWISS KINETIC TICKER MARQUEE (ANIMMASTER STYLE)
// ============================================================================

@Composable
fun SwissKineticMarquee(
    song: MusicFile?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val text = "NOW PLAYING // ${song.title.uppercase()} — ${song.artist.uppercase()}  •  SWISS AUDIO ARCHIVE  •  44.1 kHz 24-BIT FLAC  •  "

    val transition = rememberInfiniteTransition(label = "marquee_anim")
    val offsetProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlaying) 14000 else 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueeOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SwissColors.Dark)
            .border(BorderStroke(1.dp, SwissColors.Hairline), RoundedCornerShape(2.dp))
            .padding(vertical = 5.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(2.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) SwissColors.Accent else SwissColors.GrayMid)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text + text,
                color = SwissColors.GrayLight,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// SWISS HEADER
// ============================================================================

@Composable
fun SwissHeader(
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

                Text(
                    text = title.uppercase(),
                    color = SwissColors.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
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

            Text(
                text = title.uppercase(),
                color = SwissColors.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            if (itemCount != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$itemCount ENTRIES",
                    color = SwissColors.GrayMid,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
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

