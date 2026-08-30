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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile

// ============================================================================
// SWISS SONG ROW
// ============================================================================

@Composable
fun SwissSongRow(
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isCurrentSong) SwissColors.SurfaceElevated else SwissColors.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = SwissColors.White),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index Numerals
            if (index != null) {
                Text(
                    text = String.format("%02d", index + 1),
                    color = if (isCurrentSong) SwissColors.Accent else SwissColors.GrayMid,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(28.dp)
                )
            }

            // Album Thumbnail
            SwissAlbumArt(
                song = song,
                size = 42.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Artist
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song.title,
                        color = if (isCurrentSong) SwissColors.White else SwissColors.OffWhite,
                        fontSize = 13.sp,
                        fontWeight = if (isCurrentSong) FontWeight.Black else FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isCurrentSong && isPlaying) {
                        Spacer(modifier = Modifier.width(8.dp))
                        SwissAudioIndicator(
                            isPlaying = true,
                            color = SwissColors.Accent,
                            modifier = Modifier
                                .width(12.dp)
                                .height(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${song.artist} — ${song.album}",
                    color = SwissColors.GrayMid,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Duration
            Text(
                text = formatSwissTime(song.duration.toInt()),
                color = SwissColors.GrayMid,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            // Favorite Button
            IconButton(
                onClick = onFavorite,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) SwissColors.Accent else SwissColors.GrayMid,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Dropdown Menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = SwissColors.GrayMid,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .background(SwissColors.SurfaceElevated)
                        .border(BorderStroke(1.dp, SwissColors.HairlineLight), RoundedCornerShape(2.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "PLAY NEXT",
                                color = SwissColors.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
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
                                color = SwissColors.GrayLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
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

        // Hairline Row Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 62.dp)
                .height(1.dp)
                .background(SwissColors.Hairline)
        )
    }
}

// ============================================================================
// SWISS SONG LIST PAGE
// ============================================================================

@Composable
fun SwissSongListPage(
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
            .background(SwissColors.Black)
    ) {
        if (onBack != null) {
            SwissSubPageHeader(
                indexNumber = "INDEX",
                title = title,
                itemCount = songs.size,
                onBack = onBack
            )
        }

        if (songs.isEmpty()) {
            SwissEmptyView(
                title = "EMPTY TRACK LIST",
                subtitle = "ZERO AUDIO FILES REGISTERED IN STORAGE"
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
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
            }
        }
    }
}

// ============================================================================
// SWISS ARTISTS PAGE
// ============================================================================

@Composable
fun SwissArtistsPage(
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
            .background(SwissColors.Black)
    ) {
        if (artists.isEmpty()) {
            SwissEmptyView(
                title = "NO ARTISTS",
                subtitle = "NO ARTIST METADATA AVAILABLE"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(artists) { (artist, count) ->
                    SwissCard(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        onClick = { onArtistClick(artist) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(SwissColors.SurfaceElevated)
                                    .border(BorderStroke(1.dp, SwissColors.Hairline), RoundedCornerShape(2.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = SwissColors.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = artist,
                                color = SwissColors.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "$count TRACKS",
                                color = SwissColors.GrayMid,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SWISS ALBUMS PAGE
// ============================================================================

@Composable
fun SwissAlbumsPage(
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
            .background(SwissColors.Black)
    ) {
        if (albums.isEmpty()) {
            SwissEmptyView(
                title = "NO ALBUMS",
                subtitle = "NO ALBUM METADATA AVAILABLE"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(albums) { (album, artist, albumSongs) ->
                    val firstSong = albumSongs.firstOrNull()
                    SwissCard(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        onClick = { onAlbumClick(album) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SwissAlbumArt(
                                song = firstSong,
                                size = 120.dp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = album,
                                color = SwissColors.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = artist,
                                color = SwissColors.GrayMid,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            SwissBadge(text = "${albumSongs.size} TRACKS")
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SWISS QUEUE PAGE
// ============================================================================

@Composable
fun SwissQueuePage(
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
            .background(SwissColors.Black)
    ) {
        SwissSubPageHeader(
            indexNumber = "QUEUE",
            title = "PLAYBACK QUEUE",
            itemCount = queueSongs.size,
            onBack = onBack
        )

        if (queueSongs.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                SwissButton(
                    text = "Clear Queue",
                    icon = Icons.Default.Delete,
                    isPrimary = false,
                    onClick = onClearQueue
                )
            }
        }

        if (queueSongs.isEmpty()) {
            SwissEmptyView(
                title = "QUEUE EMPTY",
                subtitle = "ADD TRACKS FROM THE REPOSITORY TO QUEUE"
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(queueSongs, key = { index, s -> "${s.id}_$index" }) { index, song ->
                    val isCurrent = selectedSong?.id == song.id

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isCurrent) SwissColors.SurfaceElevated else SwissColors.Black)
                            .clickable { onSongClick(song) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("Q%02d", index + 1),
                                color = if (isCurrent) SwissColors.Accent else SwissColors.GrayMid,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )

                            SwissAlbumArt(
                                song = song,
                                size = 40.dp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    color = if (isCurrent) SwissColors.White else SwissColors.OffWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist,
                                    color = SwissColors.GrayMid,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = { onRemoveFromQueue(song) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = SwissColors.GrayMid,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 66.dp)
                                .height(1.dp)
                                .background(SwissColors.Hairline)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SWISS SEARCH PAGE
// ============================================================================

@Composable
fun SwissSearchPage(
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
            .background(SwissColors.Black)
    ) {
        SwissSubPageHeader(
            indexNumber = "RADAR",
            title = "SEARCH ARCHIVE",
            itemCount = searchResults.size,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        SwissSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (searchResults.isEmpty()) {
            SwissEmptyView(
                title = "NO MATCHES",
                subtitle = "TRY SEARCHING BY TITLE, ARTIST, OR ALBUM"
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(searchResults, key = { _, s -> s.id }) { index, song ->
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
            }
        }
    }
}
