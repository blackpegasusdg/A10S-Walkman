package com.dip.a10swalkman.ui.cyberpunk

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.MusicFile
import com.dip.a10swalkman.WalkmanPage

// ============================================================================
// CYBER MINI PLAYER
// ============================================================================

@Composable
fun CyberMiniPlayer(
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

    val shape = CyberShapes.ChamferCard

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clip(shape)
            .background(CyberColors.SurfaceElevated)
            .border(1.dp, CyberColors.NeonCyan, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = CyberColors.NeonCyan),
                onClick = onClick
            )
    ) {
        // Glowing Top Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp),
            color = CyberColors.NeonCyan,
            trackColor = CyberColors.CardBorder,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberAlbumArt(
                song = song,
                size = 42.dp,
                isPlaying = playing,
                glowColor = CyberColors.NeonCyan
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = CyberColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = CyberColors.NeonCyanDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (playing) {
                CyberAudioVisualizer(
                    isPlaying = true,
                    barCount = 4,
                    modifier = Modifier
                        .width(18.dp)
                        .height(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Quick Play/Pause
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = CyberColors.NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Quick Next
            IconButton(
                onClick = onNext,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = CyberColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ============================================================================
// CYBER BOTTOM NAVIGATION BAR
// ============================================================================

@Composable
fun CyberBottomBar(
    currentPage: WalkmanPage,
    onPageSelected: (WalkmanPage) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.Void)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(CyberColors.NeonCyan, Color.Transparent, CyberColors.NeonPink)
                    )
                )
            )
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberBottomNavItem(
                icon = Icons.Default.Home,
                label = "DECK",
                selected = currentPage == WalkmanPage.HOME,
                selectedColor = CyberColors.NeonCyan,
                onClick = { onPageSelected(WalkmanPage.HOME) }
            )

            CyberBottomNavItem(
                icon = Icons.Default.LibraryMusic,
                label = "TRACKS",
                selected = currentPage == WalkmanPage.SONGS,
                selectedColor = CyberColors.NeonCyan,
                onClick = { onPageSelected(WalkmanPage.SONGS) }
            )

            CyberBottomNavItem(
                icon = Icons.Default.Person,
                label = "ARTISTS",
                selected = currentPage == WalkmanPage.ARTISTS,
                selectedColor = CyberColors.NeonYellow,
                onClick = { onPageSelected(WalkmanPage.ARTISTS) }
            )

            CyberBottomNavItem(
                icon = Icons.Default.Album,
                label = "ALBUMS",
                selected = currentPage == WalkmanPage.ALBUMS,
                selectedColor = CyberColors.NeonPurple,
                onClick = { onPageSelected(WalkmanPage.ALBUMS) }
            )

            CyberBottomNavItem(
                icon = Icons.Default.Favorite,
                label = "FAVORITES",
                selected = currentPage == WalkmanPage.FAVORITES,
                selectedColor = CyberColors.NeonPink,
                onClick = { onPageSelected(WalkmanPage.FAVORITES) }
            )

            CyberBottomNavItem(
                icon = Icons.Default.Search,
                label = "RADAR",
                selected = currentPage == WalkmanPage.SEARCH,
                selectedColor = CyberColors.NeonGreen,
                onClick = { onPageSelected(WalkmanPage.SEARCH) }
            )
        }
    }
}

@Composable
private fun CyberBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CyberShapes.ChamferButton)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = selectedColor),
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) selectedColor else CyberColors.TextMuted,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (selected) selectedColor else CyberColors.TextMuted,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(14.dp)
                    .height(2.dp)
                    .background(selectedColor)
            )
        }
    }
}
