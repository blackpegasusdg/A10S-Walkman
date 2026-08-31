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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile
import com.dip.a10swalkman.WalkmanPage

// ============================================================================
// SWISS MINI PLAYER
// ============================================================================

@Composable
fun SwissMiniPlayer(
    song: MusicFile?,
    playing: Boolean,
    currentPosition: Int,
    duration: Int,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit
) {
    if (song == null) return

    val progress = if (duration > 0) {
        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(SwissColors.SurfaceElevated)
            .border(BorderStroke(1.dp, SwissColors.HairlineLight), RoundedCornerShape(2.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = SwissColors.White),
                onClick = onClick
            )
    ) {
        // Hairline Top Progress Line with Swiss Red active fill
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            color = SwissColors.Accent,
            trackColor = SwissColors.Hairline
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwissAlbumArt(
                song = song,
                size = 40.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = SwissColors.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = SwissColors.GrayMid,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (playing) {
                SwissAudioIndicator(
                    isPlaying = true,
                    color = SwissColors.Accent,
                    modifier = Modifier
                        .width(14.dp)
                        .height(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = SwissColors.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = SwissColors.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ============================================================================
// SWISS BOTTOM NAVIGATION BAR
// ============================================================================

@Composable
fun SwissBottomBar(
    currentPage: WalkmanPage,
    onPageSelected: (WalkmanPage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SwissColors.Black)
    ) {
        // Hairline Top Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SwissColors.Hairline)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwissBottomNavItem(
                icon = Icons.Default.Home,
                label = "DECK",
                selected = currentPage == WalkmanPage.HOME,
                onClick = { onPageSelected(WalkmanPage.HOME) }
            )

            SwissBottomNavItem(
                icon = Icons.Default.LibraryMusic,
                label = "TRACKS",
                selected = currentPage == WalkmanPage.SONGS,
                onClick = { onPageSelected(WalkmanPage.SONGS) }
            )

            SwissBottomNavItem(
                icon = Icons.Default.Person,
                label = "ARTISTS",
                selected = currentPage == WalkmanPage.ARTISTS,
                onClick = { onPageSelected(WalkmanPage.ARTISTS) }
            )

            SwissBottomNavItem(
                icon = Icons.Default.Album,
                label = "ALBUMS",
                selected = currentPage == WalkmanPage.ALBUMS,
                onClick = { onPageSelected(WalkmanPage.ALBUMS) }
            )

            SwissBottomNavItem(
                icon = Icons.Default.Favorite,
                label = "FAVORITES",
                selected = currentPage == WalkmanPage.FAVORITES,
                onClick = { onPageSelected(WalkmanPage.FAVORITES) }
            )

            SwissBottomNavItem(
                icon = Icons.Default.Search,
                label = "RADAR",
                selected = currentPage == WalkmanPage.SEARCH,
                onClick = { onPageSelected(WalkmanPage.SEARCH) }
            )
        }
    }
}

@Composable
private fun SwissBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = SwissColors.White),
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) SwissColors.White else SwissColors.GrayMid,
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (selected) SwissColors.White else SwissColors.GrayMid,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
            letterSpacing = 0.5.sp
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(12.dp)
                    .height(2.dp)
                    .background(SwissColors.Accent)
            )
        }
    }
}
