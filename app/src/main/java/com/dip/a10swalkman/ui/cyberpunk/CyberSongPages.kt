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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile

// ============================================================================
// CYBER SONG ROW
// ============================================================================

@Composable
fun CyberSongRow(
    song: MusicFile,
    index: Int? = null,
    isCurrentSong: Boolean = false,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val rowShape = CyberShapes.ChamferCard

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(rowShape)
            .background(if (isCurrentSong) CyberColors.SurfaceHighlight else CyberColors.Surface)
            .border(
                1.dp,
                if (isCurrentSong) CyberColors.NeonCyan else CyberColors.CardBorder,
                rowShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = CyberColors.NeonCyan),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index or Play Indicator
            if (index != null) {
                Text(
                    text = String.format("%02d", index + 1),
                    color = if (isCurrentSong) CyberColors.NeonCyan else CyberColors.TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(26.dp)
                )
            }

            // Thumbnail
            CyberAlbumArt(
                song = song,
                size = 46.dp,
                isPlaying = isCurrentSong && isPlaying
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Metadata & Equalizer
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    color = if (isCurrentSong) CyberColors.NeonCyan else CyberColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song.artist,
                        color = CyberColors.TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isCurrentSong && isPlaying) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CyberAudioVisualizer(
                            isPlaying = true,
                            barCount = 4,
                            modifier = Modifier
                                .width(20.dp)
                                .height(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Duration
            Text(
                text = formatCyberTime(song.duration.toInt()),
                color = CyberColors.TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )

            // Favorite Button
            IconButton(
                onClick = onFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) CyberColors.NeonPink else CyberColors.TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // More Options Dropdown
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = CyberColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .background(CyberColors.SurfaceElevated)
                        .border(1.dp, CyberColors.NeonCyan, CyberShapes.ChamferCard)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "PLAY NEXT IN QUEUE",
                                color = CyberColors.NeonCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onPlayNext()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "ADD TO QUEUE",
                                color = CyberColors.TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onAddToQueue()
                        }
                    )
                }
            }
        }
    }
}

// ============================================================================
// CYBER SONG LIST PAGE
// ============================================================================

@Composable
fun CyberSongListPage(
    title: String = "ALL TRACKS",
    songs: List<MusicFile>,
    selectedSong: MusicFile?,
    playing: Boolean,
    favoriteSongs: Set<Long>,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.Void)
    ) {
        if (onBack != null) {
            CyberSubPageHeader(
                title = title,
                itemCount = songs.size,
                onBack = onBack
            )
        }

        if (songs.isEmpty()) {
            CyberEmptyView(
                title = "NO AUDIO TRACKS FOUND",
                subtitle = "STORAGE SCAN RETURNED ZERO AUDIO RECORDS"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
            ) {
                itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
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
            }
        }
    }
}

// ============================================================================
// CYBER ARTISTS PAGE
// ============================================================================

@Composable
fun CyberArtistsPage(
    songs: List<MusicFile>,
    onArtistClick: (String) -> Unit
) {
    val artists = remember(songs) {
        songs.groupBy { it.artist }
            .map { (artist, artistSongs) -> artist to artistSongs.size }
            .sortedBy { it.first.lowercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.Void)
    ) {
        if (artists.isEmpty()) {
            CyberEmptyView(
                title = "NO ARTISTS REGISTERED",
                subtitle = "MEDIA REPOSITORY CONTAINS NO ARTIST DATA"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(artists) { (artist, count) ->
                    CyberCard(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        borderColor = CyberColors.CardBorder,
                        glowColor = CyberColors.NeonCyan,
                        onClick = { onArtistClick(artist) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CyberShapes.ChamferArtwork)
                                    .background(CyberColors.SurfaceElevated)
                                    .border(1.dp, CyberColors.NeonCyan, CyberShapes.ChamferArtwork),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = CyberColors.NeonCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = artist,
                                color = CyberColors.TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            CyberBadge(
                                text = "$count TRACKS",
                                color = CyberColors.NeonCyanDim
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// CYBER ALBUMS PAGE
// ============================================================================

@Composable
fun CyberAlbumsPage(
    songs: List<MusicFile>,
    onAlbumClick: (String) -> Unit
) {
    val albums = remember(songs) {
        songs.groupBy { it.album }
            .map { (album, albumSongs) ->
                Triple(album, albumSongs.firstOrNull()?.artist ?: "Unknown", albumSongs)
            }
            .sortedBy { it.first.lowercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.Void)
    ) {
        if (albums.isEmpty()) {
            CyberEmptyView(
                title = "NO ALBUMS FOUND",
                subtitle = "STORAGE CONTAINS NO ALBUM DATA"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(albums) { (album, artist, albumSongs) ->
                    val firstSong = albumSongs.firstOrNull()
                    CyberCard(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        borderColor = CyberColors.CardBorder,
                        glowColor = CyberColors.NeonPink,
                        onClick = { onAlbumClick(album) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CyberAlbumArt(
                                song = firstSong,
                                size = 80.dp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = album,
                                color = CyberColors.TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = artist,
                                color = CyberColors.TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            CyberBadge(
                                text = "${albumSongs.size} TRACKS",
                                color = CyberColors.NeonPink
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// CYBER QUEUE PAGE
// ============================================================================

@Composable
fun CyberQueuePage(
    queueSongs: List<MusicFile>,
    selectedSong: MusicFile?,
    playing: Boolean,
    onSongClick: (MusicFile) -> Unit,
    onRemoveFromQueue: (MusicFile) -> Unit,
    onClearQueue: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.Void)
    ) {
        CyberSubPageHeader(
            title = "PLAYBACK QUEUE",
            itemCount = queueSongs.size,
            onBack = onBack
        )

        if (queueSongs.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                CyberButton(
                    text = "PURGE QUEUE",
                    icon = Icons.Default.Delete,
                    accentColor = CyberColors.NeonPink,
                    onClick = onClearQueue
                )
            }
        }

        if (queueSongs.isEmpty()) {
            CyberEmptyView(
                title = "QUEUE BUFFER EMPTY",
                subtitle = "ADD TRACKS TO QUEUE FROM TRACK LIST OR NOW PLAYING DECK"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
            ) {
                itemsIndexed(queueSongs, key = { index, s -> "${s.id}_$index" }) { index, song ->
                    val isCurrent = selectedSong?.id == song.id
                    val rowShape = CyberShapes.ChamferCard

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(rowShape)
                            .background(if (isCurrent) CyberColors.SurfaceHighlight else CyberColors.Surface)
                            .border(1.dp, if (isCurrent) CyberColors.NeonPink else CyberColors.CardBorder, rowShape)
                            .clickable { onSongClick(song) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("Q%02d", index + 1),
                                color = if (isCurrent) CyberColors.NeonPink else CyberColors.TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )

                            CyberAlbumArt(
                                song = song,
                                size = 42.dp,
                                isPlaying = isCurrent && playing,
                                glowColor = CyberColors.NeonPink
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    color = if (isCurrent) CyberColors.NeonPink else CyberColors.TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist,
                                    color = CyberColors.TextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = { onRemoveFromQueue(song) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = CyberColors.NeonPink,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// CYBER SEARCH PAGE
// ============================================================================

@Composable
fun CyberSearchPage(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<MusicFile>,
    selectedSong: MusicFile?,
    playing: Boolean,
    favoriteSongs: Set<Long>,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.Void)
    ) {
        CyberSubPageHeader(
            title = "RADAR SCANNER",
            itemCount = searchResults.size,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(10.dp))

        CyberSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (searchResults.isEmpty()) {
            CyberEmptyView(
                title = "NO RADAR MATCHES",
                subtitle = "TRY SCANNING A DIFFERENT ARTIST, TITLE, OR ALBUM"
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(searchResults, key = { _, s -> s.id }) { index, song ->
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
            }
        }
    }
}
