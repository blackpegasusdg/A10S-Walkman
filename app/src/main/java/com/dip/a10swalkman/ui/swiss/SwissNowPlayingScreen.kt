package com.dip.a10swalkman.ui.swiss

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile
import com.dip.a10swalkman.MusicService

@Composable
fun SwissNowPlayingScreen(
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
        SwissEmptyView(
            title = "NO ACTIVE TRACK",
            subtitle = "SELECT A TRACK FROM THE ARCHIVE TO PLAY",
            actionButtonText = "RETURN TO ARCHIVE",
            onAction = onBack
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColors.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SwissIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBack,
                    size = 36.dp,
                    tint = SwissColors.White
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NOW PLAYING",
                        color = SwissColors.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    if (playing) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(SwissColors.Accent)
                        )
                    }
                }

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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CENTERPIECE SQUARE ALBUM ART
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SwissColors.SurfaceElevated)
                    .border(BorderStroke(1.dp, SwissColors.HairlineLight), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                SwissAlbumArt(
                    song = song,
                    size = 258.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TRACK METADATA
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = song.title,
                    color = SwissColors.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = song.artist,
                    color = SwissColors.GrayLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = song.album,
                    color = SwissColors.GrayMid,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // INTERACTIVE AUDIO WAVEFORM SCRUBBER
            SwissScrubber(
                currentPosition = currentPosition,
                duration = duration,
                songId = song.id,
                isPlaying = playing,
                onSeek = { service?.seekTo(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PLAYBACK CONTROLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                SwissIconButton(
                    icon = Icons.Default.Shuffle,
                    onClick = onShuffle,
                    size = 40.dp,
                    tint = if (shuffleEnabled) SwissColors.White else SwissColors.GrayMid
                )

                // Skip Previous
                SwissIconButton(
                    icon = Icons.Default.SkipPrevious,
                    onClick = { service?.playPrevious() },
                    size = 46.dp,
                    tint = SwissColors.White
                )

                // Master Solid Play/Pause Circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SwissColors.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = SwissColors.Black)
                        ) {
                            service?.togglePlayPause()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playing) "Pause" else "Play",
                        tint = SwissColors.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Skip Next
                SwissIconButton(
                    icon = Icons.Default.SkipNext,
                    onClick = { service?.playNext() },
                    size = 46.dp,
                    tint = SwissColors.White
                )

                // Repeat Mode
                SwissIconButton(
                    icon = if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    onClick = onRepeat,
                    size = 40.dp,
                    tint = if (repeatMode > 0) SwissColors.Accent else SwissColors.GrayMid
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTTOM TOOLBAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SwissBadge(
                    text = when (repeatMode) {
                        1 -> "REPEAT ALL"
                        2 -> "REPEAT ONE"
                        else -> "REPEAT OFF"
                    },
                    hasAccentDot = repeatMode > 0
                )

                IconButton(
                    onClick = { onFavorite(song) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) SwissColors.Accent else SwissColors.GrayMid,
                        modifier = Modifier.size(22.dp)
                    )
                }

                SwissBadge(
                    text = if (shuffleEnabled) "SHUFFLE ON" else "SHUFFLE OFF",
                    hasAccentDot = shuffleEnabled
                )
            }
        }
    }
}
