package com.dip.a10swalkman

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.dip.a10swalkman.ui.swiss.SwissAlbumsPage
import com.dip.a10swalkman.ui.swiss.SwissArtistsPage
import com.dip.a10swalkman.ui.swiss.SwissAuthScreen
import com.dip.a10swalkman.ui.swiss.SwissBottomBar
import com.dip.a10swalkman.ui.swiss.SwissColors
import com.dip.a10swalkman.ui.swiss.SwissHeader
import com.dip.a10swalkman.ui.swiss.SwissHomeScreen
import com.dip.a10swalkman.ui.swiss.SwissMiniPlayer
import com.dip.a10swalkman.ui.swiss.SwissNowPlayingScreen
import com.dip.a10swalkman.ui.swiss.SwissQueuePage
import com.dip.a10swalkman.ui.swiss.SwissSearchPage
import com.dip.a10swalkman.ui.swiss.SwissSongListPage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================================================
// SUPABASE CLIENT
// ============================================================================

private const val SUPABASE_URL = "https://wsgehosguxntwhfefpwj.supabase.co"
private const val SUPABASE_ANON_KEY = "sb_publishable_hRf2IAU91b_12caIdkAJAg_-K-27n9e"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    install(io.github.jan.supabase.auth.Auth)
}

// ============================================================================
// NAVIGATION ENUM
// ============================================================================

enum class WalkmanPage {
    HOME,
    SONGS,
    ARTISTS,
    ALBUMS,
    FAVORITES,
    QUEUE,
    SEARCH,
    NOW_PLAYING
}

// ============================================================================
// MAIN ACTIVITY
// ============================================================================

class MainActivity : ComponentActivity() {

    private var musicService: MusicService? = null
    private var serviceBound = false
    private val songs = mutableStateOf<List<MusicFile>>(emptyList())

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            loadMusic()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicService.MusicBinder
            if (binder != null) {
                musicService = binder.getService()
                serviceBound = true
                loadMusic()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        startMusicService()

        setContent {
            WalkmanApp(
                songs = songs.value,
                service = musicService,
                requestPermission = { requestAudioPermission() }
            )
        }

        requestAudioPermission()
    }

    private fun startMusicService() {
        val intent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                loadMusic()
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                loadMusic()
            }
        }
    }

    private fun loadMusic() {
        if (!serviceBound) return
        val loaded = try {
            musicService?.loadSongs() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        songs.value = loaded
    }

    override fun onDestroy() {
        if (serviceBound) {
            try {
                unbindService(connection)
            } catch (_: Exception) {
            }
            serviceBound = false
        }
        super.onDestroy()
    }
}

// ============================================================================
// ROOT APP COMPOSABLE
// ============================================================================

@Composable
fun WalkmanApp(
    songs: List<MusicFile>,
    service: MusicService?,
    requestPermission: () -> Unit
) {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            color = SwissColors.Black
        ) {
            var loggedIn by remember { mutableStateOf(false) }
            var checkingLogin by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                try {
                    val session = supabase.auth.currentSessionOrNull()
                    loggedIn = session != null
                } catch (_: Exception) {
                    loggedIn = false
                }
                checkingLogin = false
            }

            if (checkingLogin) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SwissColors.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = SwissColors.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LOADING ARCHIVE...",
                        color = SwissColors.GrayLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            } else if (!loggedIn) {
                SwissAuthScreen(
                    onLoginSuccess = { loggedIn = true }
                )
            } else {
                WalkmanScreen(
                    songs = songs,
                    service = service,
                    requestPermission = requestPermission,
                    onLogout = { loggedIn = false }
                )
            }
        }
    }
}

// ============================================================================
// MAIN WALKMAN SCREEN
// ============================================================================

@Composable
fun WalkmanScreen(
    songs: List<MusicFile>,
    service: MusicService?,
    requestPermission: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf(WalkmanPage.HOME) }
    var selectedArtist by remember { mutableStateOf<String?>(null) }
    var selectedAlbum by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val favoritesManager = remember { FavoritesManager(context) }
    var favoriteSongs by remember { mutableStateOf(favoritesManager.getFavorites()) }

    var selectedSong by remember { mutableStateOf<MusicFile?>(null) }
    var playing by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var shuffleEnabled by remember { mutableStateOf(service?.isShuffleEnabled() ?: false) }
    var repeatMode by remember { mutableStateOf(service?.getRepeatMode() ?: 0) }
    var queueSongs by remember { mutableStateOf<List<MusicFile>>(emptyList()) }

    // Real-time Service State Polling
    LaunchedEffect(service) {
        while (true) {
            if (service != null) {
                playing = service.isPlaying()
                currentPosition = service.getCurrentPosition()
                duration = service.getDuration()
                val current = service.getCurrentSong()
                if (current != null) {
                    selectedSong = current
                }
                shuffleEnabled = service.isShuffleEnabled()
                repeatMode = service.getRepeatMode()
                try {
                    queueSongs = service.getQueue()
                } catch (_: Exception) {
                    queueSongs = emptyList()
                }
            }
            delay(250)
        }
    }

    fun toggleFavorite(song: MusicFile) {
        val nowFav = favoritesManager.toggleFavorite(song.id)
        val updated = favoriteSongs.toMutableSet()
        if (nowFav) updated.add(song.id) else updated.remove(song.id)
        favoriteSongs = updated
    }

    fun playSong(song: MusicFile) {
        selectedSong = song
        service?.playSong(song, songs)
    }

    fun addToQueue(song: MusicFile) {
        try {
            service?.addToQueue(song)
            queueSongs = service?.getQueue() ?: queueSongs
        } catch (_: Exception) {
        }
    }

    fun playNextInQueue(song: MusicFile) {
        try {
            service?.playNext(song)
            queueSongs = service?.getQueue() ?: queueSongs
        } catch (_: Exception) {
        }
    }

    fun removeFromQueue(song: MusicFile) {
        try {
            service?.removeFromQueue(song)
            queueSongs = service?.getQueue() ?: queueSongs
        } catch (_: Exception) {
        }
    }

    fun clearQueue() {
        try {
            service?.clearQueue()
            queueSongs = emptyList()
        } catch (_: Exception) {
        }
    }

    val searchResults = remember(songs, searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            songs
        } else {
            val q = searchQuery.trim().lowercase()
            songs.filter {
                it.title.lowercase().contains(q) ||
                        it.artist.lowercase().contains(q) ||
                        it.album.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColors.Black)
    ) {
        // TOP SWISS HEADER BAR
        if (currentPage != WalkmanPage.NOW_PLAYING) {
            SwissHeader(
                indexNumber = when (currentPage) {
                    WalkmanPage.HOME -> "01"
                    WalkmanPage.SONGS -> "02"
                    WalkmanPage.ARTISTS -> "03"
                    WalkmanPage.ALBUMS -> "04"
                    WalkmanPage.FAVORITES -> "05"
                    WalkmanPage.QUEUE -> "06"
                    WalkmanPage.SEARCH -> "07"
                    WalkmanPage.NOW_PLAYING -> "08"
                },
                title = when (currentPage) {
                    WalkmanPage.HOME -> "WALKMAN"
                    WalkmanPage.SONGS -> "TRACKS"
                    WalkmanPage.ARTISTS -> "ARTISTS"
                    WalkmanPage.ALBUMS -> "ALBUMS"
                    WalkmanPage.FAVORITES -> "FAVORITES"
                    WalkmanPage.QUEUE -> "QUEUE"
                    WalkmanPage.SEARCH -> "RADAR"
                    WalkmanPage.NOW_PLAYING -> "PLAYING"
                },
                queueCount = queueSongs.size,
                onSearchClick = { currentPage = WalkmanPage.SEARCH },
                onQueueClick = { currentPage = WalkmanPage.QUEUE },
                onLogoutClick = {
                    scope.launch {
                        try {
                            supabase.auth.signOut()
                        } catch (_: Exception) {
                        }
                        onLogout()
                    }
                },
                onBackClick = if (selectedArtist != null || selectedAlbum != null || currentPage != WalkmanPage.HOME) {
                    {
                        if (selectedArtist != null) {
                            selectedArtist = null
                        } else if (selectedAlbum != null) {
                            selectedAlbum = null
                        } else {
                            currentPage = WalkmanPage.HOME
                        }
                    }
                } else null
            )
        }

        // MAIN SWISS CONTENT AREA
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentPage) {
                WalkmanPage.HOME -> {
                    SwissHomeScreen(
                        songs = songs,
                        selectedSong = selectedSong,
                        playing = playing,
                        currentPosition = currentPosition,
                        duration = duration,
                        favoriteSongs = favoriteSongs,
                        service = service,
                        shuffleEnabled = shuffleEnabled,
                        repeatMode = repeatMode,
                        onNavigate = { currentPage = it },
                        onSongClick = { playSong(it) },
                        onFavorite = { toggleFavorite(it) },
                        onAddToQueue = { addToQueue(it) },
                        onPlayNext = { playNextInQueue(it) },
                        onShuffle = { shuffleEnabled = service?.toggleShuffle() ?: shuffleEnabled },
                        onRepeat = { repeatMode = service?.cycleRepeatMode() ?: repeatMode },
                        onOpenNowPlaying = {
                            if (selectedSong != null) currentPage = WalkmanPage.NOW_PLAYING
                        },
                        onRequestPermission = requestPermission
                    )
                }

                WalkmanPage.SONGS -> {
                    SwissSongListPage(
                        title = "ALL TRACKS",
                        songs = songs,
                        selectedSong = selectedSong,
                        playing = playing,
                        favoriteSongs = favoriteSongs,
                        onSongClick = { playSong(it) },
                        onFavorite = { toggleFavorite(it) },
                        onAddToQueue = { addToQueue(it) },
                        onPlayNext = { playNextInQueue(it) }
                    )
                }

                WalkmanPage.ARTISTS -> {
                    if (selectedArtist != null) {
                        val artistSongs = songs.filter { it.artist == selectedArtist }
                        SwissSongListPage(
                            title = "ARTIST // $selectedArtist",
                            songs = artistSongs,
                            selectedSong = selectedSong,
                            playing = playing,
                            favoriteSongs = favoriteSongs,
                            onSongClick = { playSong(it) },
                            onFavorite = { toggleFavorite(it) },
                            onAddToQueue = { addToQueue(it) },
                            onPlayNext = { playNextInQueue(it) },
                            onBack = { selectedArtist = null }
                        )
                    } else {
                        SwissArtistsPage(
                            songs = songs,
                            onArtistClick = { selectedArtist = it }
                        )
                    }
                }

                WalkmanPage.ALBUMS -> {
                    if (selectedAlbum != null) {
                        val albumSongs = songs.filter { it.album == selectedAlbum }
                        SwissSongListPage(
                            title = "ALBUM // $selectedAlbum",
                            songs = albumSongs,
                            selectedSong = selectedSong,
                            playing = playing,
                            favoriteSongs = favoriteSongs,
                            onSongClick = { playSong(it) },
                            onFavorite = { toggleFavorite(it) },
                            onAddToQueue = { addToQueue(it) },
                            onPlayNext = { playNextInQueue(it) },
                            onBack = { selectedAlbum = null }
                        )
                    } else {
                        SwissAlbumsPage(
                            songs = songs,
                            onAlbumClick = { selectedAlbum = it }
                        )
                    }
                }

                WalkmanPage.FAVORITES -> {
                    val favList = songs.filter { favoriteSongs.contains(it.id) }
                    SwissSongListPage(
                        title = "FAVORITE TRACKS",
                        songs = favList,
                        selectedSong = selectedSong,
                        playing = playing,
                        favoriteSongs = favoriteSongs,
                        onSongClick = { playSong(it) },
                        onFavorite = { toggleFavorite(it) },
                        onAddToQueue = { addToQueue(it) },
                        onPlayNext = { playNextInQueue(it) }
                    )
                }

                WalkmanPage.QUEUE -> {
                    SwissQueuePage(
                        queueSongs = queueSongs,
                        selectedSong = selectedSong,
                        playing = playing,
                        onSongClick = { playSong(it) },
                        onRemoveFromQueue = { removeFromQueue(it) },
                        onClearQueue = { clearQueue() },
                        onBack = { currentPage = WalkmanPage.HOME }
                    )
                }

                WalkmanPage.SEARCH -> {
                    SwissSearchPage(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        searchResults = searchResults,
                        selectedSong = selectedSong,
                        playing = playing,
                        favoriteSongs = favoriteSongs,
                        onSongClick = { playSong(it) },
                        onFavorite = { toggleFavorite(it) },
                        onAddToQueue = { addToQueue(it) },
                        onPlayNext = { playNextInQueue(it) },
                        onBack = { currentPage = WalkmanPage.HOME }
                    )
                }

                WalkmanPage.NOW_PLAYING -> {
                    SwissNowPlayingScreen(
                        song = selectedSong,
                        playing = playing,
                        currentPosition = currentPosition,
                        duration = duration,
                        isFavorite = selectedSong?.let { favoriteSongs.contains(it.id) } ?: false,
                        shuffleEnabled = shuffleEnabled,
                        repeatMode = repeatMode,
                        queueCount = queueSongs.size,
                        service = service,
                        onBack = { currentPage = WalkmanPage.HOME },
                        onFavorite = { toggleFavorite(it) },
                        onShuffle = { shuffleEnabled = service?.toggleShuffle() ?: shuffleEnabled },
                        onRepeat = { repeatMode = service?.cycleRepeatMode() ?: repeatMode },
                        onQueueClick = { currentPage = WalkmanPage.QUEUE }
                    )
                }
            }
        }

        // DOCKED FLOATING MINI PLAYER & BOTTOM NAVIGATION BAR
        if (currentPage != WalkmanPage.NOW_PLAYING) {
            AnimatedVisibility(
                visible = selectedSong != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SwissMiniPlayer(
                    song = selectedSong,
                    playing = playing,
                    currentPosition = currentPosition,
                    duration = duration,
                    onTogglePlay = { service?.togglePlayPause() },
                    onNext = { service?.playNext() },
                    onClick = { currentPage = WalkmanPage.NOW_PLAYING }
                )
            }

            SwissBottomBar(
                currentPage = currentPage,
                onPageSelected = {
                    selectedArtist = null
                    selectedAlbum = null
                    currentPage = it
                }
            )
        }
    }
}
