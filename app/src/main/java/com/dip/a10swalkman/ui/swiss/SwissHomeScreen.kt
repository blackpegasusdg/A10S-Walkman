package com.dip.a10swalkman.ui.swiss

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile
import com.dip.a10swalkman.MusicService
import com.dip.a10swalkman.WalkmanPage

@Composable
fun SwissHomeScreen(
    songs: List<MusicFile>,
    selectedSong: MusicFile?,
    playing: Boolean,
    currentPosition: Int,
    duration: Int,
    favoriteSongs: Set<Long>,
    service: MusicService?,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    onNavigate: (WalkmanPage) -> Unit,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onOpenNowPlaying: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val activeSong = selectedSong ?: songs.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColors.Black)
    ) {
        // 1. HERO NOW PLAYING CARD
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "01 / ",
                            color = SwissColors.Accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "CURRENT RECORD",
                            color = SwissColors.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    SwissBadge(
                        text = if (playing) "ACTIVE" else "PAUSED",
                        hasAccentDot = playing
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeSong != null) {
                    SwissCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenNowPlaying
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SwissAlbumArt(
                                    song = activeSong,
                                    size = 64.dp
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeSong.title,
                                        color = SwissColors.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = activeSong.artist,
                                        color = SwissColors.GrayLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = activeSong.album,
                                        color = SwissColors.GrayMid,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Waveform Scrubber
                            SwissScrubber(
                                currentPosition = currentPosition,
                                duration = duration,
                                songId = activeSong.id,
                                isPlaying = playing,
                                onSeek = { service?.seekTo(it) }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Playback Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SwissIconButton(
                                    icon = Icons.Default.Shuffle,
                                    onClick = onShuffle,
                                    size = 36.dp,
                                    tint = if (shuffleEnabled) SwissColors.White else SwissColors.GrayMid
                                )

                                SwissIconButton(
                                    icon = Icons.Default.SkipPrevious,
                                    onClick = { service?.playPrevious() },
                                    size = 38.dp,
                                    tint = SwissColors.White
                                )

                                // Play/Pause Circle
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(SwissColors.White)
                                        .clickable {
                                            if (selectedSong == null && songs.isNotEmpty()) {
                                                onSongClick(songs.first())
                                            } else {
                                                service?.togglePlayPause()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Toggle",
                                        tint = SwissColors.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                SwissIconButton(
                                    icon = Icons.Default.SkipNext,
                                    onClick = { service?.playNext() },
                                    size = 38.dp,
                                    tint = SwissColors.White
                                )

                                SwissIconButton(
                                    icon = if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                    onClick = onRepeat,
                                    size = 36.dp,
                                    tint = if (repeatMode > 0) SwissColors.Accent else SwissColors.GrayMid
                                )
                            }
                        }
                    }
                } else {
                    SwissCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onRequestPermission
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "NO AUDIO DETECTED",
                                color = SwissColors.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Grant permission to read audio files from local storage.",
                                color = SwissColors.GrayMid,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            SwissButton(
                                text = "Grant Access",
                                onClick = onRequestPermission
                            )
                        }
                    }
                }
            }
        }

        // 2. SECTORS GRID
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "02 / ",
                        color = SwissColors.Accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "LIBRARY SECTORS",
                        color = SwissColors.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SwissNavTile(
                        index = "01",
                        title = "TRACKS",
                        subtitle = "${songs.size} ENTRIES",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.SONGS) }
                    )

                    SwissNavTile(
                        index = "02",
                        title = "FAVORITES",
                        subtitle = "${favoriteSongs.size} ENTRIES",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.FAVORITES) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val artistsCount = remember(songs) { songs.map { it.artist }.distinct().size }
                    val albumsCount = remember(songs) { songs.map { it.album }.distinct().size }

                    SwissNavTile(
                        index = "03",
                        title = "ARTISTS",
                        subtitle = "$artistsCount ENTRIES",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.ARTISTS) }
                    )

                    SwissNavTile(
                        index = "04",
                        title = "ALBUMS",
                        subtitle = "$albumsCount ENTRIES",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.ALBUMS) }
                    )
                }
            }
        }

        // 3. CHRONOLOGICAL RECENT INDEX
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "03 / ",
                            color = SwissColors.Accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "RECENT ENTRIES",
                            color = SwissColors.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "VIEW ALL →",
                        color = SwissColors.GrayMid,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate(WalkmanPage.SONGS) }
                    )
                }
            }
        }

        val previewSongs = songs.take(15)
        itemsIndexed(previewSongs, key = { _, s -> s.id }) { index, song ->
            SwissSongRow(
                song = song,
                index = index,
                isCurrentSong = selectedSong?.id == song.id,
                isPlaying = playing,
                isFavorite = favoriteSongs.contains(song.id),
                onClick = { onSongClick(song) },
                onFavorite = { onFavorite(song) },
                onAddToQueue = { onAddToQueue(song) },
                onPlayNext = { onPlayNext(song) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SwissNavTile(
    index: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    SwissCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "$index /",
                color = SwissColors.Accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = SwissColors.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = SwissColors.GrayMid,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
