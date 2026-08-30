package com.dip.a10swalkman.ui.cyberpunk

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile
import com.dip.a10swalkman.MusicService

@Composable
fun CyberNowPlayingScreen(
    song: MusicFile?,
    playing: Boolean,
    currentPosition: Int,
    duration: Int,
    isFavorite: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    queueCount: Int,
    service: MusicService?,
    onBack: () -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onQueueClick: () -> Unit
) {
    if (song == null) {
        CyberEmptyView(
            title = "DECK IDLE",
            subtitle = "NO AUDIO STREAM CURRENTLY INITIALIZED",
            actionButtonText = "RETURN TO DECK",
            onAction = onBack
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.Void)
            .drawCyberGrid()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBack,
                    size = 38.dp,
                    tint = CyberColors.NeonCyan
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NOW PLAYING // DECK",
                        color = CyberColors.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (playing) "STATUS: STREAMING // 96kHz" else "STATUS: PAUSED",
                        color = if (playing) CyberColors.NeonGreen else CyberColors.NeonPink,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                Box {
                    CyberIconButton(
                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                        onClick = onQueueClick,
                        size = 38.dp,
                        tint = if (queueCount > 0) CyberColors.NeonPink else CyberColors.TextSecondary
                    )
                    if (queueCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
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

            Spacer(modifier = Modifier.height(12.dp))

            // CENTERPIECE ALBUM ART
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CyberShapes.ChamferArtwork)
                    .background(CyberColors.SurfaceElevated)
                    .border(
                        2.dp,
                        Brush.sweepGradient(
                            listOf(
                                CyberColors.NeonCyan,
                                CyberColors.NeonPink,
                                CyberColors.NeonCyan
                            )
                        ),
                        CyberShapes.ChamferArtwork
                    )
                    .drawCyberBrackets(
                        bracketColor = if (playing) CyberColors.NeonCyan else CyberColors.TextMuted,
                        bracketLength = 20.dp,
                        strokeWidth = 2.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                CyberAlbumArt(
                    song = song,
                    size = 236.dp,
                    isPlaying = playing,
                    glowColor = CyberColors.NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SPECTRUM VISUALIZER
            CyberAudioVisualizer(
                isPlaying = playing,
                barCount = 24,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // TRACK INFO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = song.title,
                    color = CyberColors.NeonCyan,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = song.artist,
                    color = CyberColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "// ${song.album}",
                    color = CyberColors.TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PROGRESS SCRUBBER
            CyberScrubber(
                currentPosition = currentPosition,
                duration = duration,
                onSeek = { service?.seekTo(it) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // PLAYBACK CONTROLS DECK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button
                CyberIconButton(
                    icon = Icons.Default.Shuffle,
                    onClick = onShuffle,
                    size = 44.dp,
                    tint = if (shuffleEnabled) CyberColors.NeonCyan else CyberColors.TextMuted,
                    borderColor = if (shuffleEnabled) CyberColors.NeonCyan else CyberColors.CardBorder
                )

                // Skip Previous
                CyberIconButton(
                    icon = Icons.Default.SkipPrevious,
                    onClick = { service?.playPrevious() },
                    size = 50.dp,
                    tint = CyberColors.TextPrimary
                )

                // Master Hex Play/Pause
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CyberShapes.ChamferCard)
                        .background(
                            Brush.linearGradient(
                                listOf(CyberColors.NeonCyan, CyberColors.NeonPink)
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White)
                        ) {
                            service?.togglePlayPause()
                        }
                        .drawCyberBrackets(Color.White, bracketLength = 10.dp, strokeWidth = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playing) "Pause" else "Play",
                        tint = CyberColors.Void,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Skip Next
                CyberIconButton(
                    icon = Icons.Default.SkipNext,
                    onClick = { service?.playNext() },
                    size = 50.dp,
                    tint = CyberColors.TextPrimary
                )

                // Repeat Mode Button
                CyberIconButton(
                    icon = if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    onClick = onRepeat,
                    size = 44.dp,
                    tint = if (repeatMode > 0) CyberColors.NeonPink else CyberColors.TextMuted,
                    borderColor = if (repeatMode > 0) CyberColors.NeonPink else CyberColors.CardBorder
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTTOM TOOLBAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberBadge(
                    text = when (repeatMode) {
                        1 -> "REPEAT: ALL"
                        2 -> "REPEAT: ONE"
                        else -> "REPEAT: OFF"
                    },
                    color = if (repeatMode > 0) CyberColors.NeonPink else CyberColors.TextMuted
                )

                IconButton(
                    onClick = { onFavorite(song) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) CyberColors.NeonPink else CyberColors.TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                CyberBadge(
                    text = if (shuffleEnabled) "SHUFFLE: ON" else "SHUFFLE: OFF",
                    color = if (shuffleEnabled) CyberColors.NeonCyan else CyberColors.TextMuted
                )
            }
        }
    }
}
