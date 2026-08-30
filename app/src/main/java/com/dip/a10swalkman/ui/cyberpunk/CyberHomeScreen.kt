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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile
import com.dip.a10swalkman.MusicService
import com.dip.a10swalkman.WalkmanPage

@Composable
fun CyberHomeScreen(
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
            .background(CyberColors.Void)
            .drawCyberGrid()
    ) {
        // 1. HERO NOW DECODING CARD
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">> CURRENT TRANSMISSION",
                        color = CyberColors.NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    CyberBadge(
                        text = if (playing) "CORE // ONLINE" else "CORE // STANDBY",
                        color = if (playing) CyberColors.NeonGreen else CyberColors.TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeSong != null) {
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (playing) CyberColors.NeonCyan else CyberColors.CardBorder,
                        glowColor = CyberColors.NeonCyan,
                        onClick = onOpenNowPlaying
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CyberAlbumArt(
                                    song = activeSong,
                                    size = 72.dp,
                                    isPlaying = playing,
                                    glowColor = CyberColors.NeonCyan
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeSong.title,
                                        color = CyberColors.NeonCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = activeSong.artist,
                                        color = CyberColors.TextPrimary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "// ${activeSong.album}",
                                        color = CyberColors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mini Visualizer
                            CyberAudioVisualizer(
                                isPlaying = playing,
                                barCount = 20,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mini Scrubber
                            CyberScrubber(
                                currentPosition = currentPosition,
                                duration = duration,
                                onSeek = { service?.seekTo(it) }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mini Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CyberIconButton(
                                    icon = Icons.Default.Shuffle,
                                    onClick = onShuffle,
                                    size = 36.dp,
                                    tint = if (shuffleEnabled) CyberColors.NeonCyan else CyberColors.TextMuted
                                )

                                CyberIconButton(
                                    icon = Icons.Default.SkipPrevious,
                                    onClick = { service?.playPrevious() },
                                    size = 38.dp,
                                    tint = CyberColors.TextPrimary
                                )

                                // Master Play/Pause
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CyberShapes.ChamferCard)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(CyberColors.NeonCyan, CyberColors.NeonPink)
                                            )
                                        )
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
                                        tint = CyberColors.Void,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                CyberIconButton(
                                    icon = Icons.Default.SkipNext,
                                    onClick = { service?.playNext() },
                                    size = 38.dp,
                                    tint = CyberColors.TextPrimary
                                )

                                CyberIconButton(
                                    icon = if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                    onClick = onRepeat,
                                    size = 36.dp,
                                    tint = if (repeatMode > 0) CyberColors.NeonPink else CyberColors.TextMuted
                                )
                            }
                        }
                    }
                } else {
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onRequestPermission
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "NO AUDIO TRANSMISSIONS DETECTED",
                                color = CyberColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CyberButton(
                                text = "GRANT STORAGE ACCESS",
                                onClick = onRequestPermission
                            )
                        }
                    }
                }
            }
        }

        // 2. QUICK NAVIGATION TILES
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = ">> REPOSITORY SECTORS",
                    color = CyberColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberNavTile(
                        title = "TRACKS",
                        subtitle = "${songs.size}",
                        icon = Icons.Default.LibraryMusic,
                        accentColor = CyberColors.NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.SONGS) }
                    )

                    CyberNavTile(
                        title = "FAVORITES",
                        subtitle = "${favoriteSongs.size}",
                        icon = Icons.Default.Favorite,
                        accentColor = CyberColors.NeonPink,
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

                    CyberNavTile(
                        title = "ARTISTS",
                        subtitle = "$artistsCount",
                        icon = Icons.Default.Person,
                        accentColor = CyberColors.NeonYellow,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.ARTISTS) }
                    )

                    CyberNavTile(
                        title = "ALBUMS",
                        subtitle = "$albumsCount",
                        icon = Icons.Default.Album,
                        accentColor = CyberColors.NeonPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(WalkmanPage.ALBUMS) }
                    )
                }
            }
        }

        // 3. RECENT TRANSMISSIONS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">> RECENT TRANSMISSIONS",
                        color = CyberColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "[VIEW ALL]",
                        color = CyberColors.NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { onNavigate(WalkmanPage.SONGS) }
                    )
                }
            }
        }

        val previewSongs = songs.take(15)
        itemsIndexed(previewSongs, key = { _, s -> s.id }) { index, song ->
            CyberSongRow(
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CyberNavTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.35f),
        glowColor = accentColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CyberShapes.ChamferButton)
                    .background(CyberColors.SurfaceElevated)
                    .border(1.dp, accentColor, CyberShapes.ChamferButton),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    color = CyberColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$subtitle ENTRIES",
                    color = CyberColors.TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
