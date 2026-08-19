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
import androidx.core.view.WindowCompat

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


// ============================================================================
// MAIN ACTIVITY
// ============================================================================

class MainActivity : ComponentActivity() {

    private var musicService: MusicService? = null

    private var serviceBound = false

    private var songs =
        mutableStateOf<List<MusicFile>>(emptyList())


    // =========================================================================
    // PERMISSION
    // =========================================================================

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                loadMusic()
            }
        }


    // =========================================================================
    // SERVICE CONNECTION
    // =========================================================================

    private val connection =
        object : ServiceConnection {

            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?
            ) {

                val binder =
                    service as? MusicService.MusicBinder

                if (binder != null) {

                    musicService =
                        binder.getService()

                    serviceBound = true

                    loadMusic()
                }
            }


            override fun onServiceDisconnected(
                name: ComponentName?
            ) {

                musicService = null

                serviceBound = false
            }
        }


    // =========================================================================
    // CREATE
    // =========================================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        startMusicService()


        setContent {

            WalkmanApp(

                songs = songs.value,

                service = musicService,

                requestPermission = {
                    requestAudioPermission()
                }
            )
        }


        requestAudioPermission()
    }


    // =========================================================================
    // START SERVICE
    // =========================================================================

    private fun startMusicService() {

        val intent =
            Intent(
                this,
                MusicService::class.java
            )


        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            startForegroundService(intent)

        } else {

            startService(intent)
        }


        bindService(

            intent,

            connection,

            Context.BIND_AUTO_CREATE
        )
    }


    // =========================================================================
    // PERMISSION
    // =========================================================================

    private fun requestAudioPermission() {

        if (
            Build.VERSION.SDK_INT >= 33
        ) {

            if (
                checkSelfPermission(
                    Manifest.permission.READ_MEDIA_AUDIO
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {

                permissionLauncher.launch(
                    Manifest.permission.READ_MEDIA_AUDIO
                )

            } else {

                loadMusic()
            }

        } else {

            if (
                checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {

                permissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )

            } else {

                loadMusic()
            }
        }
    }


    // =========================================================================
    // LOAD MUSIC
    // =========================================================================

    private fun loadMusic() {

        if (!serviceBound) {
            return
        }


        val loaded =
            try {

                musicService?.loadSongs()
                    ?: emptyList()

            } catch (_: Exception) {

                emptyList()
            }


        songs.value = loaded
    }


    // =========================================================================
    // DESTROY
    // =========================================================================

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
// APP
// ============================================================================

@Composable
fun WalkmanApp(
    songs: List<MusicFile>,
    service: MusicService?,
    requestPermission: () -> Unit
) {

    MaterialTheme {

        Surface(

            modifier =
                Modifier.fillMaxSize(),

            color =
                Color(0xFF080808)
        ) {

            WalkmanScreen(

                songs = songs,

                service = service,

                requestPermission =
                    requestPermission
            )
        }
    }
}


// ============================================================================
// NAVIGATION
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
// MAIN SCREEN
// ============================================================================

@Composable
fun WalkmanScreen(
    songs: List<MusicFile>,
    service: MusicService?,
    requestPermission: () -> Unit
) {

    val context =
        LocalContext.current


    // =========================================================================
    // NAVIGATION
    // =========================================================================

    var currentPage by remember {

        mutableStateOf(
            WalkmanPage.HOME
        )
    }


    var selectedArtist by remember {

        mutableStateOf<String?>(null)
    }


    var selectedAlbum by remember {

        mutableStateOf<String?>(null)
    }


    // =========================================================================
    // SEARCH
    // =========================================================================

    var searchQuery by remember {

        mutableStateOf("")
    }


    // =========================================================================
    // FAVORITES
    // =========================================================================

    val preferences =
        remember {

            context.getSharedPreferences(
                "a10s_walkman",
                Context.MODE_PRIVATE
            )
        }


    var favoriteSongs by remember {

        mutableStateOf(

            preferences
                .getStringSet(
                    "favorites",
                    emptySet()
                )
                ?.mapNotNull {
                    it.toLongOrNull()
                }
                ?.toSet()
                ?: emptySet()
        )
    }


    // =========================================================================
    // PLAYER STATE
    // =========================================================================

    var selectedSong by remember {

        mutableStateOf<MusicFile?>(null)
    }


    var playing by remember {

        mutableStateOf(false)
    }


    var currentPosition by remember {

        mutableStateOf(0)
    }


    var duration by remember {

        mutableStateOf(0)
    }


    var shuffleEnabled by remember {

        mutableStateOf(
            service?.isShuffleEnabled()
                ?: false
        )
    }


    var repeatMode by remember {

        mutableStateOf(
            service?.getRepeatMode()
                ?: 0
        )
    }


    // =========================================================================
    // QUEUE STATE
    // =========================================================================
    //
    // The actual queue will be maintained by MusicService.
    // MainActivity observes it here.
    //
    // These calls will be implemented in MusicService.kt next.
    //

    var queueSongs by remember {

        mutableStateOf<List<MusicFile>>(
            emptyList()
        )
    }


    // =========================================================================
    // PLAYER POLLING
    // =========================================================================

    LaunchedEffect(service) {

        while (true) {

            if (service != null) {

                playing =
                    service.isPlaying()


                currentPosition =
                    service.getCurrentPosition()


                duration =
                    service.getDuration()


                val serviceSong =
                    service.getCurrentSong()


                if (serviceSong != null) {

                    selectedSong =
                        serviceSong
                }


                shuffleEnabled =
                    service.isShuffleEnabled()


                repeatMode =
                    service.getRepeatMode()


                // -------------------------------------------------------------
                // QUEUE
                // -------------------------------------------------------------

                try {

                    queueSongs =
                        service.getQueue()

                } catch (_: Exception) {

                    queueSongs =
                        emptyList()
                }
            }


            delay(300)
        }
    }


    // =========================================================================
    // FAVORITE
    // =========================================================================

    fun toggleFavorite(
        song: MusicFile
    ) {

        val newFavorites =
            favoriteSongs.toMutableSet()


        if (
            newFavorites.contains(song.id)
        ) {

            newFavorites.remove(song.id)

        } else {

            newFavorites.add(song.id)
        }


        favoriteSongs =
            newFavorites


        preferences.edit()
            .putStringSet(
                "favorites",
                newFavorites
                    .map {
                        it.toString()
                    }
                    .toSet()
            )
            .apply()
    }


    // =========================================================================
    // PLAY SONG
    // =========================================================================

    fun playSong(
        song: MusicFile
    ) {

        selectedSong =
            song

        service?.playSong(
            song,
            songs
        )
    }


    // =========================================================================
    // ADD TO QUEUE
    // =========================================================================

    fun addToQueue(
        song: MusicFile
    ) {

        try {

            service?.addToQueue(song)

            queueSongs =
                service?.getQueue()
                    ?: queueSongs

        } catch (_: Exception) {
        }
    }


    // =========================================================================
    // PLAY NEXT
    // =========================================================================

    fun playNextInQueue(
        song: MusicFile
    ) {

        try {

            service?.playNext(song)

            queueSongs =
                service?.getQueue()
                    ?: queueSongs

        } catch (_: Exception) {
        }
    }


    // =========================================================================
    // REMOVE FROM QUEUE
    // =========================================================================

    fun removeFromQueue(
        song: MusicFile
    ) {

        try {

            service?.removeFromQueue(song)

            queueSongs =
                service?.getQueue()
                    ?: queueSongs

        } catch (_: Exception) {
        }
    }


    // =========================================================================
    // CLEAR QUEUE
    // =========================================================================

    fun clearQueue() {

        try {

            service?.clearQueue()

            queueSongs =
                service?.getQueue()
                    ?: emptyList()

        } catch (_: Exception) {
        }
    }


    // =========================================================================
    // SEARCH RESULTS
    // =========================================================================

    val searchResults =
        remember(
            songs,
            searchQuery
        ) {

            if (
                searchQuery
                    .trim()
                    .isEmpty()
            ) {

                songs

            } else {

                val query =
                    searchQuery
                        .trim()
                        .lowercase()

                songs.filter {

                    it.title
                        .lowercase()
                        .contains(query)

                            ||

                            it.artist
                                .lowercase()
                                .contains(query)

                            ||

                            it.album
                                .lowercase()
                                .contains(query)
                }
            }
        }


    // =========================================================================
    // LAYOUT
    // =========================================================================

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF080808)
                )
    ) {


        // =====================================================================
        // HEADER
        // =====================================================================

        if (
            currentPage !=
            WalkmanPage.NOW_PLAYING
        ) {

            WalkmanHeader(

                page = currentPage,

                queueCount =
                    queueSongs.size,

                onSearch = {

                    currentPage =
                        WalkmanPage.SEARCH
                },

                onQueue = {

                    currentPage =
                        WalkmanPage.QUEUE
                },

                onBack = {

                    if (
                        selectedArtist != null
                    ) {

                        selectedArtist = null

                    } else if (
                        selectedAlbum != null
                    ) {

                        selectedAlbum = null

                    } else {

                        currentPage =
                            WalkmanPage.HOME
                    }
                }
            )
        }


        // =====================================================================
        // CONTENT
        // =====================================================================

        Box(

            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
        ) {

            when (currentPage) {


                // =================================================================
                // HOME
                // =================================================================

                WalkmanPage.HOME -> {

                    HomePage(

                        songs = songs,

                        selectedSong =
                            selectedSong,

                        playing = playing,

                        currentPosition =
                            currentPosition,

                        duration = duration,

                        favoriteSongs =
                            favoriteSongs,

                        service = service,

                        shuffleEnabled =
                            shuffleEnabled,

                        repeatMode =
                            repeatMode,

                        onSongClick = {
                            playSong(it)
                        },

                        onFavorite = {
                            toggleFavorite(it)
                        },

                        onAddToQueue = {
                            addToQueue(it)
                        },

                        onPlayNext = {
                            playNextInQueue(it)
                        },

                        onShuffle = {

                            shuffleEnabled =
                                service?.toggleShuffle()
                                    ?: shuffleEnabled
                        },

                        onRepeat = {

                            repeatMode =
                                service?.cycleRepeatMode()
                                    ?: repeatMode
                        },

                        onOpenNowPlaying = {

                            if (
                                selectedSong != null
                            ) {

                                currentPage =
                                    WalkmanPage.NOW_PLAYING
                            }
                        }
                    )
                }


                // =================================================================
                // SONGS
                // =================================================================

                WalkmanPage.SONGS -> {

                    SongListPage(

                        title = "SONGS",

                        songs = songs,

                        selectedSong =
                            selectedSong,

                        favoriteSongs =
                            favoriteSongs,

                        service = service,

                        onSongClick = {
                            playSong(it)
                        },

                        onFavorite = {
                            toggleFavorite(it)
                        },

                        onAddToQueue = {
                            addToQueue(it)
                        },

                        onPlayNext = {
                            playNextInQueue(it)
                        },

                        emptyMessage =
                            "No songs found"
                    )
                }


                // =================================================================
                // ARTISTS
                // =================================================================

                WalkmanPage.ARTISTS -> {

                    if (
                        selectedArtist == null
                    ) {

                        ArtistsPage(

                            songs = songs,

                            onArtistClick = {

                                selectedArtist =
                                    it
                            }
                        )

                    } else {

                        ArtistSongsPage(

                            artist =
                                selectedArtist!!,

                            songs = songs,

                            selectedSong =
                                selectedSong,

                            favoriteSongs =
                                favoriteSongs,

                            onBack = {

                                selectedArtist =
                                    null
                            },

                            onSongClick = {
                                playSong(it)
                            },

                            onFavorite = {
                                toggleFavorite(it)
                            },

                            onAddToQueue = {
                                addToQueue(it)
                            },

                            onPlayNext = {
                                playNextInQueue(it)
                            }
                        )
                    }
                }


                // =================================================================
                // ALBUMS
                // =================================================================

                WalkmanPage.ALBUMS -> {

                    if (
                        selectedAlbum == null
                    ) {

                        AlbumsPage(

                            songs = songs,

                            onAlbumClick = {

                                selectedAlbum =
                                    it
                            }
                        )

                    } else {

                        AlbumSongsPage(

                            album =
                                selectedAlbum!!,

                            songs = songs,

                            selectedSong =
                                selectedSong,

                            favoriteSongs =
                                favoriteSongs,

                            onBack = {

                                selectedAlbum =
                                    null
                            },

                            onSongClick = {
                                playSong(it)
                            },

                            onFavorite = {
                                toggleFavorite(it)
                            },

                            onAddToQueue = {
                                addToQueue(it)
                            },

                            onPlayNext = {
                                playNextInQueue(it)
                            }
                        )
                    }
                }


                // =================================================================
                // FAVORITES
                // =================================================================

                WalkmanPage.FAVORITES -> {

                    val favoriteList =
                        songs.filter {

                            favoriteSongs.contains(
                                it.id
                            )
                        }


                    SongListPage(

                        title = "FAVORITES",

                        songs = favoriteList,

                        selectedSong =
                            selectedSong,

                        favoriteSongs =
                            favoriteSongs,

                        service = service,

                        onSongClick = {
                            playSong(it)
                        },

                        onFavorite = {
                            toggleFavorite(it)
                        },

                        onAddToQueue = {
                            addToQueue(it)
                        },

                        onPlayNext = {
                            playNextInQueue(it)
                        },

                        emptyMessage =
                            "No favorite songs yet"
                    )
                }


                // =================================================================
                // QUEUE
                // =================================================================

                WalkmanPage.QUEUE -> {

                    QueuePage(

                        queueSongs =
                            queueSongs,

                        selectedSong =
                            selectedSong,

                        playing =
                            playing,

                        onSongClick = {

                            playSong(it)
                        },

                        onRemove = {

                            removeFromQueue(it)
                        },

                        onClear = {

                            clearQueue()
                        }
                    )
                }


                // =================================================================
                // SEARCH
                // =================================================================

                WalkmanPage.SEARCH -> {

                    SearchPage(

                        query =
                            searchQuery,

                        results =
                            searchResults,

                        selectedSong =
                            selectedSong,

                        favoriteSongs =
                            favoriteSongs,

                        onQueryChange = {

                            searchQuery =
                                it
                        },

                        onClear = {

                            searchQuery = ""
                        },

                        onSongClick = {

                            playSong(it)
                        },

                        onFavorite = {

                            toggleFavorite(it)
                        },

                        onAddToQueue = {

                            addToQueue(it)
                        },

                        onPlayNext = {

                            playNextInQueue(it)
                        }
                    )
                }


                // =================================================================
                // NOW PLAYING
                // =================================================================

                WalkmanPage.NOW_PLAYING -> {

                    if (
                        selectedSong != null
                    ) {

                        NowPlayingScreen(

                            song =
                                selectedSong!!,

                            playing =
                                playing,

                            currentPosition =
                                currentPosition,

                            duration =
                                duration,

                            favorite =
                                favoriteSongs.contains(
                                    selectedSong!!.id
                                ),

                            shuffleEnabled =
                                shuffleEnabled,

                            repeatMode =
                                repeatMode,

                            service =
                                service,

                            onFavorite = {

                                toggleFavorite(
                                    selectedSong!!
                                )
                            },

                            onShuffle = {

                                shuffleEnabled =
                                    service
                                        ?.toggleShuffle()
                                        ?: shuffleEnabled
                            },

                            onRepeat = {

                                repeatMode =
                                    service
                                        ?.cycleRepeatMode()
                                        ?: repeatMode
                            },

                            onBack = {

                                currentPage =
                                    WalkmanPage.HOME
                            }
                        )
                    }
                }
            }
        }


        // =====================================================================
        // MINI PLAYER
        // =====================================================================

        if (
            selectedSong != null &&
            currentPage != WalkmanPage.HOME &&
            currentPage != WalkmanPage.NOW_PLAYING
        ) {

            MiniPlayer(

                song =
                    selectedSong!!,

                playing =
                    playing,

                service =
                    service,

                onPlayPause = {

                    if (playing) {

                        service?.pause()

                    } else {

                        service?.resume()
                    }
                },

                onOpenNowPlaying = {

                    currentPage =
                        WalkmanPage.NOW_PLAYING
                }
            )
        }


        // =====================================================================
        // BOTTOM BAR
        // =====================================================================

        if (
            currentPage !=
            WalkmanPage.NOW_PLAYING &&
            currentPage !=
            WalkmanPage.SEARCH &&
            currentPage !=
            WalkmanPage.QUEUE
        ) {

            WalkmanBottomBar(

                currentPage =
                    currentPage,

                onPageSelected = {

                    selectedArtist = null

                    selectedAlbum = null

                    currentPage = it
                }
            )
        }
    }
}


// ============================================================================
// HEADER
// ============================================================================

@Composable
fun WalkmanHeader(
    page: WalkmanPage,
    queueCount: Int,
    onSearch: () -> Unit,
    onQueue: () -> Unit,
    onBack: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 15.dp,
                    vertical = 10.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        if (
            page != WalkmanPage.HOME
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(

                    Icons.Default.ArrowBack,

                    contentDescription =
                        "Back",

                    tint =
                        Color.White
                )
            }
        }


        Column {

            Text(

                text = "A10S",

                color =
                    Color.White,

                fontSize =
                    25.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text = "WALKMAN",

                color =
                    Color(0xFF888888),

                fontSize =
                    10.sp,

                letterSpacing =
                    3.sp
            )
        }


        Spacer(
            modifier =
                Modifier.weight(1f)
        )


        // SEARCH BUTTON

        IconButton(
            onClick =
                onSearch
        ) {

            Icon(

                Icons.Default.Search,

                contentDescription =
                    "Search",

                tint =
                    Color.White
            )
        }


        // QUEUE BUTTON

        Box {

            IconButton(
                onClick =
                    onQueue
            ) {

                Icon(

                    Icons.Default.QueueMusic,

                    contentDescription =
                        "Queue",

                    tint =
                        Color.White
                )
            }


            if (
                queueCount > 0
            ) {

                Box(

                    modifier =
                        Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White
                            )
                            .align(
                                Alignment.TopEnd
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            if (queueCount > 99)
                                "99+"
                            else
                                queueCount.toString(),

                        color =
                            Color.Black,

                        fontSize =
                            7.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }


        Text(

            text =
                when (page) {

                    WalkmanPage.HOME ->
                        "MUSIC"

                    WalkmanPage.SONGS ->
                        "SONGS"

                    WalkmanPage.ARTISTS ->
                        "ARTISTS"

                    WalkmanPage.ALBUMS ->
                        "ALBUMS"

                    WalkmanPage.FAVORITES ->
                        "FAVORITES"

                    WalkmanPage.QUEUE ->
                        "QUEUE"

                    WalkmanPage.SEARCH ->
                        "SEARCH"

                    WalkmanPage.NOW_PLAYING ->
                        "NOW PLAYING"
                },

            color =
                Color(0xFF666666),

            fontSize =
                9.sp,

            letterSpacing =
                1.sp
        )
    }
}


// ============================================================================
// HOME
// ============================================================================

@Composable
fun HomePage(
    songs: List<MusicFile>,
    selectedSong: MusicFile?,
    playing: Boolean,
    currentPosition: Int,
    duration: Int,
    favoriteSongs: Set<Long>,
    service: MusicService?,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onOpenNowPlaying: () -> Unit
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        PlayerSection(

            song =
                selectedSong,

            playing =
                playing,

            currentPosition =
                currentPosition,

            duration =
                duration,

            service =
                service,

            favorite =
                selectedSong?.let {

                    favoriteSongs.contains(
                        it.id
                    )

                } ?: false,

            shuffleEnabled =
                shuffleEnabled,

            repeatMode =
                repeatMode,

            onPlayFirst = {

                if (songs.isNotEmpty()) {

                    onSongClick(
                        songs.first()
                    )
                }
            },

            onFavorite = {

                selectedSong?.let {
                    onFavorite(it)
                }
            },

            onShuffle =
                onShuffle,

            onRepeat =
                onRepeat,

            onOpenNowPlaying =
                onOpenNowPlaying
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        SectionTitle(
            title = "RECENT MUSIC"
        )


        if (songs.isEmpty()) {

            EmptyMusicView(
                requestPermission = {}
            )

        } else {

            LazyColumn(

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                items(

                    items =
                        songs.take(8),

                    key = {
                        it.id
                    }

                ) { song ->

                    SongRow(

                        song =
                            song,

                        selected =
                            selectedSong?.id ==
                                    song.id,

                        favorite =
                            favoriteSongs.contains(
                                song.id
                            ),

                        onClick = {
                            onSongClick(song)
                        },

                        onFavorite = {
                            onFavorite(song)
                        },

                        onAddToQueue = {
                            onAddToQueue(song)
                        },

                        onPlayNext = {
                            onPlayNext(song)
                        }
                    )
                }
            }
        }
    }
}


// ============================================================================
// PLAYER SECTION
// ============================================================================

@Composable
fun PlayerSection(
    song: MusicFile?,
    playing: Boolean,
    currentPosition: Int,
    duration: Int,
    service: MusicService?,
    favorite: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    onPlayFirst: () -> Unit,
    onFavorite: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onOpenNowPlaying: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    if (song != null) {
                        onOpenNowPlaying()
                    }
                }
                .padding(
                    horizontal = 20.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        WalkmanAlbumArt(

            song =
                song,

            modifier =
                Modifier.size(210.dp)
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        Text(

            text =
                song?.title
                    ?: "Nothing Playing",

            color =
                Color.White,

            fontSize =
                21.sp,

            fontWeight =
                FontWeight.Bold,

            maxLines =
                1,

            overflow =
                TextOverflow.Ellipsis
        )


        Text(

            text =
                song?.artist
                    ?: "Select a song",

            color =
                Color(0xFF999999),

            fontSize =
                14.sp,

            maxLines =
                1,

            overflow =
                TextOverflow.Ellipsis
        )


        if (
            song != null &&
            song.album.isNotBlank()
        ) {

            Text(

                text =
                    song.album,

                color =
                    Color(0xFF777777),

                fontSize =
                    12.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Slider(

            value =
                if (duration > 0) {

                    currentPosition
                        .toFloat()
                        .coerceIn(
                            0f,
                            duration.toFloat()
                        )

                } else {

                    0f
                },

            onValueChange = {

                service?.seekTo(
                    it.toInt()
                )
            },

            valueRange =
                0f..maxOf(
                    duration.toFloat(),
                    1f
                ),

            modifier =
                Modifier.fillMaxWidth()
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(

                text =
                    formatTime(
                        currentPosition
                    ),

                color =
                    Color.Gray,

                fontSize =
                    11.sp
            )


            Text(

                text =
                    formatTime(
                        duration
                    ),

                color =
                    Color.Gray,

                fontSize =
                    11.sp
            )
        }


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceEvenly,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onShuffle
            ) {

                Icon(

                    Icons.Default.Shuffle,

                    contentDescription =
                        "Shuffle",

                    tint =
                        if (shuffleEnabled)
                            Color.White
                        else
                            Color(0xFF666666)
                )
            }


            IconButton(

                onClick = {
                    service?.playPrevious()
                }

            ) {

                Icon(

                    Icons.Default.SkipPrevious,

                    contentDescription =
                        "Previous",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(34.dp)
                )
            }


            IconButton(

                onClick = {

                    if (playing) {

                        service?.pause()

                    } else {

                        if (song == null) {

                            onPlayFirst()

                        } else {

                            service?.resume()
                        }
                    }
                },

                modifier =
                    Modifier
                        .size(60.dp)
                        .clip(
                            CircleShape
                        )
                        .background(
                            Color.White
                        )
            ) {

                Icon(

                    imageVector =
                        if (playing)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,

                    contentDescription =
                        "Play Pause",

                    tint =
                        Color.Black,

                    modifier =
                        Modifier.size(34.dp)
                )
            }


            IconButton(

                onClick = {
                    service?.playNext()
                }

            ) {

                Icon(

                    Icons.Default.SkipNext,

                    contentDescription =
                        "Next",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(34.dp)
                )
            }


            IconButton(
                onClick =
                    onRepeat
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(

                        Icons.Default.Repeat,

                        contentDescription =
                            "Repeat",

                        tint =
                            if (repeatMode != 0)
                                Color.White
                            else
                                Color(0xFF666666)
                    )


                    if (repeatMode == 1) {

                        Text(
                            text = "ALL",
                            color =
                                Color.White,
                            fontSize = 7.sp
                        )

                    } else if (repeatMode == 2) {

                        Text(
                            text = "ONE",
                            color =
                                Color.White,
                            fontSize = 7.sp
                        )
                    }
                }
            }
        }


        if (song != null) {

            IconButton(
                onClick =
                    onFavorite
            ) {

                Icon(

                    imageVector =
                        if (favorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,

                    contentDescription =
                        "Favorite",

                    tint =
                        if (favorite)
                            Color.White
                        else
                            Color(0xFF777777),

                    modifier =
                        Modifier.size(26.dp)
                )
            }
        }
    }
}


// ============================================================================
// SEARCH PAGE
// ============================================================================

@Composable
fun SearchPage(
    query: String,
    results: List<MusicFile>,
    selectedSong: MusicFile?,
    favoriteSongs: Set<Long>,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit
) {

    Column(

        modifier =
            Modifier.fillMaxSize()
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 15.dp,
                        vertical = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            TextField(

                value =
                    query,

                onValueChange =
                    onQueryChange,

                modifier =
                    Modifier.weight(1f),

                singleLine =
                    true,

                placeholder = {

                    Text(
                        "Search songs, artists, albums",
                        color =
                            Color(0xFF777777)
                    )
                },

                leadingIcon = {

                    Icon(

                        Icons.Default.Search,

                        contentDescription =
                            null,

                        tint =
                            Color.White
                    )
                },

                trailingIcon = {

                    if (query.isNotEmpty()) {

                        IconButton(
                            onClick =
                                onClear
                        ) {

                            Icon(

                                Icons.Default.Clear,

                                contentDescription =
                                    "Clear",

                                tint =
                                    Color.White
                            )
                        }
                    }
                },

                colors =
                    TextFieldDefaults.colors(

                        focusedTextColor =
                            Color.White,

                        unfocusedTextColor =
                            Color.White,

                        focusedContainerColor =
                            Color(0xFF181818),

                        unfocusedContainerColor =
                            Color(0xFF181818),

                        focusedIndicatorColor =
                            Color.Transparent,

                        unfocusedIndicatorColor =
                            Color.Transparent,

                        cursorColor =
                            Color.White
                    ),

                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            )
        }


        if (query.isEmpty()) {

            Column(

                modifier =
                    Modifier.fillMaxSize(),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Icon(

                    Icons.Default.Search,

                    contentDescription =
                        null,

                    tint =
                        Color(0xFF555555),

                    modifier =
                        Modifier.size(60.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Text(

                    text =
                        "Search your music",

                    color =
                        Color.Gray,

                    fontSize =
                        16.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        "Songs • Artists • Albums",

                    color =
                        Color(0xFF555555),

                    fontSize =
                        12.sp
                )
            }

        } else {

            Text(

                text =
                    "${results.size} results",

                color =
                    Color.Gray,

                fontSize =
                    12.sp,

                modifier =
                    Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    )
            )


            if (results.isEmpty()) {

                EmptyMessage(
                    text =
                        "No music matches \"$query\""
                )

            } else {

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    items(

                        items =
                            results,

                        key = {
                            it.id
                        }

                    ) { song ->

                        SongRow(

                            song =
                                song,

                            selected =
                                selectedSong?.id ==
                                        song.id,

                            favorite =
                                favoriteSongs.contains(
                                    song.id
                                ),

                            onClick = {
                                onSongClick(song)
                            },

                            onFavorite = {
                                onFavorite(song)
                            },

                            onAddToQueue = {
                                onAddToQueue(song)
                            },

                            onPlayNext = {
                                onPlayNext(song)
                            }
                        )
                    }
                }
            }
        }
    }
}


// ============================================================================
// QUEUE PAGE
// ============================================================================

@Composable
fun QueuePage(
    queueSongs: List<MusicFile>,
    selectedSong: MusicFile?,
    playing: Boolean,
    onSongClick: (MusicFile) -> Unit,
    onRemove: (MusicFile) -> Unit,
    onClear: () -> Unit
) {

    Column(

        modifier =
            Modifier.fillMaxSize()
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        "PLAY QUEUE",

                    color =
                        Color.White,

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        "${queueSongs.size} songs",

                    color =
                        Color.Gray,

                    fontSize =
                        12.sp
                )
            }


            if (queueSongs.isNotEmpty()) {

                IconButton(
                    onClick =
                        onClear
                ) {

                    Icon(

                        Icons.Default.Delete,

                        contentDescription =
                            "Clear queue",

                        tint =
                            Color(0xFFAAAAAA)
                    )
                }
            }
        }


        if (queueSongs.isEmpty()) {

            Column(

                modifier =
                    Modifier.fillMaxSize(),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Icon(

                    Icons.Default.QueueMusic,

                    contentDescription =
                        null,

                    tint =
                        Color(0xFF555555),

                    modifier =
                        Modifier.size(65.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Text(

                    text =
                        "Queue is empty",

                    color =
                        Color.White,

                    fontSize =
                        18.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        "Add songs from your library",

                    color =
                        Color.Gray,

                    fontSize =
                        13.sp
                )
            }

        } else {

            LazyColumn(

                modifier =
                    Modifier.fillMaxSize()
            ) {

                items(

                    items =
                        queueSongs,

                    key = {
                        "queue_${it.id}"
                    }

                ) { song ->

                    QueueSongRow(

                        song =
                            song,

                        selected =
                            selectedSong?.id ==
                                    song.id,

                        playing =
                            playing &&
                                    selectedSong?.id ==
                                    song.id,

                        onClick = {
                            onSongClick(song)
                        },

                        onRemove = {
                            onRemove(song)
                        }
                    )
                }
            }
        }
    }
}


// ============================================================================
// QUEUE SONG ROW
// ============================================================================

@Composable
fun QueueSongRow(
    song: MusicFile,
    selected: Boolean,
    playing: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(

                    if (selected)
                        Color(0xFF202020)
                    else
                        Color.Transparent
                )
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 18.dp,
                    vertical = 9.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        WalkmanAlbumArt(

            song =
                song,

            modifier =
                Modifier.size(52.dp)
        )


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    song.title,

                color =
                    if (playing)
                        Color.White
                    else
                        Color.White,

                fontSize =
                    15.sp,

                fontWeight =
                    if (selected)
                        FontWeight.Bold
                    else
                        FontWeight.Normal,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Text(

                text =
                    song.artist.ifBlank {
                        "Unknown Artist"
                    },

                color =
                    Color(0xFF888888),

                fontSize =
                    11.sp,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }


        Text(

            text =
                formatTimeLong(
                    song.duration
                ),

            color =
                Color(0xFF666666),

            fontSize =
                10.sp
        )


        IconButton(
            onClick =
                onRemove
        ) {

            Icon(

                Icons.Default.Remove,

                contentDescription =
                    "Remove from queue",

                tint =
                    Color(0xFF777777)
            )
        }
    }
}


// ============================================================================
// SONG LIST
// ============================================================================

@Composable
fun SongListPage(
    title: String,
    songs: List<MusicFile>,
    selectedSong: MusicFile?,
    favoriteSongs: Set<Long>,
    service: MusicService?,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit,
    emptyMessage: String
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        SectionTitle(
            title = title
        )


        if (songs.isEmpty()) {

            EmptyMessage(
                text =
                    emptyMessage
            )

        } else {

            LazyColumn(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {

                items(

                    items = songs,

                    key = {
                        it.id
                    }

                ) { song ->

                    SongRow(

                        song =
                            song,

                        selected =
                            selectedSong?.id ==
                                    song.id,

                        favorite =
                            favoriteSongs.contains(
                                song.id
                            ),

                        onClick = {
                            onSongClick(song)
                        },

                        onFavorite = {
                            onFavorite(song)
                        },

                        onAddToQueue = {
                            onAddToQueue(song)
                        },

                        onPlayNext = {
                            onPlayNext(song)
                        }
                    )
                }
            }
        }
    }
}


// ============================================================================
// SONG ROW
// ============================================================================

@Composable
fun SongRow(
    song: MusicFile,
    selected: Boolean,
    favorite: Boolean,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit
) {

    var showActions by remember(
        song.id
    ) {

        mutableStateOf(false)
    }


    Column {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(

                        if (selected)
                            Color(0xFF202020)
                        else
                            Color.Transparent
                    )
                    .clickable {
                        onClick()
                    }
                    .padding(
                        horizontal = 18.dp,
                        vertical = 9.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            WalkmanAlbumArt(

                song =
                    song,

                modifier =
                    Modifier.size(54.dp)
            )


            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        song.title,

                    color =
                        Color.White,

                    fontSize =
                        16.sp,

                    fontWeight =
                        if (selected)
                            FontWeight.Bold
                        else
                            FontWeight.Normal,

                    maxLines =
                        1,

                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(

                    text =
                        song.artist.ifBlank {
                            "Unknown Artist"
                        },

                    color =
                        Color(0xFF999999),

                    fontSize =
                        12.sp,

                    maxLines =
                        1,

                    overflow =
                        TextOverflow.Ellipsis
                )


                if (
                    song.album.isNotBlank()
                ) {

                    Text(

                        text =
                            song.album,

                        color =
                            Color(0xFF666666),

                        fontSize =
                            10.sp,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }


            Text(

                text =
                    formatTimeLong(
                        song.duration
                    ),

                color =
                    Color(0xFF666666),

                fontSize =
                    10.sp
            )


            IconButton(

                onClick = {
                    showActions =
                        !showActions
                }

            ) {

                Icon(

                    Icons.Default.QueueMusic,

                    contentDescription =
                        "Queue",

                    tint =
                        Color(0xFF777777),

                    modifier =
                        Modifier.size(21.dp)
                )
            }


            IconButton(
                onClick =
                    onFavorite
            ) {

                Icon(

                    imageVector =
                        if (favorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,

                    contentDescription =
                        "Favorite",

                    tint =
                        if (favorite)
                            Color.White
                        else
                            Color(0xFF555555),

                    modifier =
                        Modifier.size(21.dp)
                )
            }
        }


        // =====================================================================
        // QUEUE ACTIONS
        // =====================================================================

        if (showActions) {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF181818)
                        )
                        .padding(
                            horizontal = 70.dp,
                            vertical = 5.dp
                        ),

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                Row(

                    modifier =
                        Modifier.clickable {

                            onPlayNext()

                            showActions =
                                false
                        }
                            .padding(
                                8.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        Icons.Default.SkipNext,

                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(18.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )


                    Text(

                        text =
                            "Play Next",

                        color =
                            Color.White,

                        fontSize =
                            11.sp
                    )
                }


                Row(

                    modifier =
                        Modifier.clickable {

                            onAddToQueue()

                            showActions =
                                false
                        }
                            .padding(
                                8.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        Icons.Default.Add,

                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(18.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )


                    Text(

                        text =
                            "Add to Queue",

                        color =
                            Color.White,

                        fontSize =
                            11.sp
                    )
                }
            }
        }
    }
}


// ============================================================================
// MINI PLAYER
// ============================================================================

@Composable
fun MiniPlayer(
    song: MusicFile,
    playing: Boolean,
    service: MusicService?,
    onPlayPause: () -> Unit,
    onOpenNowPlaying: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF151515)
                )
                .clickable {
                    onOpenNowPlaying()
                }
                .padding(
                    horizontal = 12.dp,
                    vertical = 7.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        WalkmanAlbumArt(

            song =
                song,

            modifier =
                Modifier.size(45.dp)
        )


        Spacer(
            modifier =
                Modifier.width(10.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    song.title,

                color =
                    Color.White,

                fontSize =
                    13.sp,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Text(

                text =
                    song.artist,

                color =
                    Color(0xFF777777),

                fontSize =
                    10.sp,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }


        IconButton(
            onClick =
                onPlayPause
        ) {

            Icon(

                imageVector =
                    if (playing)
                        Icons.Default.Pause
                    else
                        Icons.Default.PlayArrow,

                contentDescription =
                    "Play",

                tint =
                    Color.White
            )
        }


        IconButton(

            onClick = {
                service?.playNext()
            }

        ) {

            Icon(

                Icons.Default.SkipNext,

                contentDescription =
                    "Next",

                tint =
                    Color.White
            )
        }
    }
}


// ============================================================================
// ARTISTS
// ============================================================================

@Composable
fun ArtistsPage(
    songs: List<MusicFile>,
    onArtistClick: (String) -> Unit
) {

    val artists =
        songs
            .map {
                it.artist.ifBlank {
                    "Unknown Artist"
                }
            }
            .distinct()
            .sorted()


    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        SectionTitle(
            title = "ARTISTS"
        )


        if (artists.isEmpty()) {

            EmptyMessage(
                text =
                    "No artists found"
            )

        } else {

            LazyColumn(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {

                items(
                    items = artists
                ) { artist ->

                    ArtistRow(

                        artist =
                            artist,

                        songCount =
                            songs.count {

                                it.artist.ifBlank {
                                    "Unknown Artist"
                                } == artist
                            },

                        onClick = {

                            onArtistClick(
                                artist
                            )
                        }
                    )
                }
            }
        }
    }
}


// ============================================================================
// ARTIST SONGS
// ============================================================================

@Composable
fun ArtistSongsPage(
    artist: String,
    songs: List<MusicFile>,
    selectedSong: MusicFile?,
    favoriteSongs: Set<Long>,
    onBack: () -> Unit,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit
) {

    val artistSongs =
        songs.filter {

            it.artist.ifBlank {
                "Unknown Artist"
            } == artist
        }


    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        SubPageHeader(

            title =
                artist,

            subtitle =
                "${artistSongs.size} songs",

            onBack =
                onBack
        )


        if (artistSongs.isEmpty()) {

            EmptyMessage(
                text =
                    "No songs found"
            )

        } else {

            LazyColumn(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {

                items(

                    items =
                        artistSongs,

                    key = {
                        it.id
                    }

                ) { song ->

                    SongRow(

                        song =
                            song,

                        selected =
                            selectedSong?.id ==
                                    song.id,

                        favorite =
                            favoriteSongs.contains(
                                song.id
                            ),

                        onClick = {
                            onSongClick(song)
                        },

                        onFavorite = {
                            onFavorite(song)
                        },

                        onAddToQueue = {
                            onAddToQueue(song)
                        },

                        onPlayNext = {
                            onPlayNext(song)
                        }
                    )
                }
            }
        }
    }
}


// ============================================================================
// ALBUMS
// ============================================================================

@Composable
fun AlbumsPage(
    songs: List<MusicFile>,
    onAlbumClick: (String) -> Unit
) {

    val albums =
        songs
            .map {
                it.album.ifBlank {
                    "Unknown Album"
                }
            }
            .distinct()
            .sorted()


    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        SectionTitle(
            title = "ALBUMS"
        )


        if (albums.isEmpty()) {

            EmptyMessage(
                text =
                    "No albums found"
            )

        } else {

            LazyColumn(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {

                items(
                    items = albums
                ) { album ->

                    val albumSong =
                        songs.firstOrNull {

                            it.album.ifBlank {
                                "Unknown Album"
                            } == album
                        }


                    AlbumRow(

                        album =
                            album,

                        artist =
                            albumSong?.artist
                                ?: "Unknown Artist",

                        songCount =
                            songs.count {

                                it.album.ifBlank {
                                    "Unknown Album"
                                } == album
                            },

                        song =
                            albumSong,

                        onClick = {

                            onAlbumClick(
                                album
                            )
                        }
                    )
                }
            }
        }
    }
}


// ============================================================================
// ALBUM SONGS
// ============================================================================

@Composable
fun AlbumSongsPage(
    album: String,
    songs: List<MusicFile>,
    selectedSong: MusicFile?,
    favoriteSongs: Set<Long>,
    onBack: () -> Unit,
    onSongClick: (MusicFile) -> Unit,
    onFavorite: (MusicFile) -> Unit,
    onAddToQueue: (MusicFile) -> Unit,
    onPlayNext: (MusicFile) -> Unit
) {

    val albumSongs =
        songs.filter {

            it.album.ifBlank {
                "Unknown Album"
            } == album
        }


    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        SubPageHeader(

            title =
                album,

            subtitle =
                "${albumSongs.size} songs",

            onBack =
                onBack
        )


        LazyColumn(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
        ) {

            items(

                items =
                    albumSongs,

                key = {
                    it.id
                }

            ) { song ->

                SongRow(

                    song =
                        song,

                    selected =
                        selectedSong?.id ==
                                song.id,

                    favorite =
                        favoriteSongs.contains(
                            song.id
                        ),

                    onClick = {
                        onSongClick(song)
                    },

                    onFavorite = {
                        onFavorite(song)
                    },

                    onAddToQueue = {
                        onAddToQueue(song)
                    },

                    onPlayNext = {
                        onPlayNext(song)
                    }
                )
            }
        }
    }
}


// ============================================================================
// ARTIST ROW
// ============================================================================

@Composable
fun ArtistRow(
    artist: String,
    songCount: Int,
    onClick: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(

            modifier =
                Modifier
                    .size(54.dp)
                    .clip(
                        CircleShape
                    )
                    .background(
                        Color(0xFF202020)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                Icons.Default.Person,

                contentDescription =
                    null,

                tint =
                    Color.White,

                modifier =
                    Modifier.size(28.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.width(15.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    artist,

                color =
                    Color.White,

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Medium,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Text(

                text =
                    "$songCount songs",

                color =
                    Color.Gray,

                fontSize =
                    12.sp
            )
        }
    }
}


// ============================================================================
// ALBUM ROW
// ============================================================================

@Composable
fun AlbumRow(
    album: String,
    artist: String,
    songCount: Int,
    song: MusicFile?,
    onClick: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        WalkmanAlbumArt(

            song =
                song,

            modifier =
                Modifier.size(64.dp)
        )


        Spacer(
            modifier =
                Modifier.width(15.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    album,

                color =
                    Color.White,

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Medium,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Text(

                text =
                    artist,

                color =
                    Color.Gray,

                fontSize =
                    13.sp,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Text(

                text =
                    "$songCount songs",

                color =
                    Color(0xFF666666),

                fontSize =
                    11.sp
            )
        }
    }
}


// ============================================================================
// BOTTOM NAVIGATION
// ============================================================================

@Composable
fun WalkmanBottomBar(
    currentPage: WalkmanPage,
    onPageSelected:
        (WalkmanPage) -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF111111)
                )
                .padding(
                    vertical = 8.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {

        BottomItem(

            Icons.Default.Home,

            "Home",

            currentPage ==
                    WalkmanPage.HOME

        ) {

            onPageSelected(
                WalkmanPage.HOME
            )
        }


        BottomItem(

            Icons.Default.LibraryMusic,

            "Songs",

            currentPage ==
                    WalkmanPage.SONGS

        ) {

            onPageSelected(
                WalkmanPage.SONGS
            )
        }


        BottomItem(

            Icons.Default.Person,

            "Artists",

            currentPage ==
                    WalkmanPage.ARTISTS

        ) {

            onPageSelected(
                WalkmanPage.ARTISTS
            )
        }


        BottomItem(

            Icons.Default.Album,

            "Albums",

            currentPage ==
                    WalkmanPage.ALBUMS

        ) {

            onPageSelected(
                WalkmanPage.ALBUMS
            )
        }


        BottomItem(

            Icons.Default.Favorite,

            "Favorites",

            currentPage ==
                    WalkmanPage.FAVORITES

        ) {

            onPageSelected(
                WalkmanPage.FAVORITES
            )
        }
    }
}


// ============================================================================
// BOTTOM ITEM
// ============================================================================

@Composable
fun BottomItem(
    icon:
    androidx.compose.ui.graphics
    .vector.ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 6.dp,
                    vertical = 3.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(

            imageVector =
                icon,

            contentDescription =
                text,

            tint =
                if (selected)
                    Color.White
                else
                    Color(0xFF666666),

            modifier =
                Modifier.size(22.dp)
        )


        Text(

            text =
                text,

            color =
                if (selected)
                    Color.White
                else
                    Color(0xFF666666),

            fontSize =
                10.sp,

            textAlign =
                TextAlign.Center
        )
    }
}


// ============================================================================
// SUB PAGE HEADER
// ============================================================================

@Composable
fun SubPageHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 15.dp,
                    vertical = 10.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick =
                onBack
        ) {

            Icon(

                Icons.Default.ArrowBack,

                contentDescription =
                    "Back",

                tint =
                    Color.White
            )
        }


        Column {

            Text(

                text =
                    title,

                color =
                    Color.White,

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Text(

                text =
                    subtitle,

                color =
                    Color.Gray,

                fontSize =
                    12.sp
            )
        }
    }
}


// ============================================================================
// SECTION TITLE
// ============================================================================

@Composable
fun SectionTitle(
    title: String
) {

    Text(

        text =
            title,

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                ),

        color =
            Color.White,

        fontSize =
            14.sp,

        fontWeight =
            FontWeight.Bold,

        letterSpacing =
            1.5.sp
    )
}


// ============================================================================
// ALBUM ART
// ============================================================================

@Composable
fun WalkmanAlbumArt(
    song: MusicFile?,
    modifier: Modifier =
        Modifier
) {

    val context =
        LocalContext.current


    var bitmap by remember(
        song?.id
    ) {

        mutableStateOf<
                android.graphics.Bitmap?
                >(null)
    }


    LaunchedEffect(
        song?.id
    ) {

        bitmap = null

        if (song != null) {

            bitmap =
                withContext(
                    Dispatchers.IO
                ) {

                    try {

                        AlbumArt.getArtwork(

                            context =
                                context,

                            song =
                                song
                        )

                    } catch (_: Exception) {

                        null
                    }
                }
        }
    }


    Box(

        modifier =
            modifier
                .clip(
                    RoundedCornerShape(
                        10.dp
                    )
                )
                .background(
                    Color(0xFF181818)
                ),

        contentAlignment =
            Alignment.Center
    ) {

        val currentBitmap =
            bitmap


        if (
            currentBitmap != null
        ) {

            Image(

                bitmap =
                    currentBitmap
                        .asImageBitmap(),

                contentDescription =
                    "Album artwork",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )

        } else {

            Icon(

                Icons.Default.MusicNote,

                contentDescription =
                    null,

                tint =
                    Color(0xFF666666),

                modifier =
                    Modifier.size(50.dp)
            )
        }
    }
}


// ============================================================================
// EMPTY MUSIC
// ============================================================================

@Composable
fun EmptyMusicView(
    requestPermission: () -> Unit
) {

    Column(

        modifier =
            Modifier.fillMaxSize(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Icon(

            Icons.Default.LibraryMusic,

            contentDescription =
                null,

            tint =
                Color.Gray,

            modifier =
                Modifier.size(60.dp)
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        Text(

            text =
                "No music found",

            color =
                Color.White,

            fontSize =
                18.sp
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Text(

            text =
                "Put music files on your phone",

            color =
                Color.Gray
        )
    }
}


// ============================================================================
// EMPTY MESSAGE
// ============================================================================

@Composable
fun EmptyMessage(
    text: String
) {

    Column(

        modifier =
            Modifier.fillMaxSize(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Icon(

            Icons.Default.MusicNote,

            contentDescription =
                null,

            tint =
                Color(0xFF555555),

            modifier =
                Modifier.size(55.dp)
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        Text(

            text =
                text,

            color =
                Color.Gray,

            fontSize =
                16.sp,

            textAlign =
                TextAlign.Center,

            modifier =
                Modifier.padding(
                    horizontal = 30.dp
                )
        )
    }
}


// ============================================================================
// NOW PLAYING
// ============================================================================

@Composable
fun NowPlayingScreen(
    song: MusicFile,
    playing: Boolean,
    currentPosition: Int,
    duration: Int,
    favorite: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    service: MusicService?,
    onFavorite: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onBack: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF080808)
                )
                .padding(
                    horizontal = 20.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onBack
            ) {

                Icon(

                    Icons.Default.ArrowBack,

                    contentDescription =
                        "Back",

                    tint =
                        Color.White
                )
            }


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            Text(

                text =
                    "NOW PLAYING",

                color =
                    Color(0xFF999999),

                fontSize =
                    11.sp,

                fontWeight =
                    FontWeight.Bold,

                letterSpacing =
                    2.sp
            )


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            IconButton(
                onClick =
                    onFavorite
            ) {

                Icon(

                    imageVector =
                        if (favorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,

                    contentDescription =
                        "Favorite",

                    tint =
                        if (favorite)
                            Color.White
                        else
                            Color(0xFF777777)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        WalkmanAlbumArt(

            song =
                song,

            modifier =
                Modifier
                    .size(300.dp)
                    .clip(
                        RoundedCornerShape(
                            16.dp
                        )
                    )
        )


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        Text(

            text =
                song.title,

            color =
                Color.White,

            fontSize =
                24.sp,

            fontWeight =
                FontWeight.Bold,

            maxLines =
                1,

            overflow =
                TextOverflow.Ellipsis
        )


        Text(

            text =
                song.artist.ifBlank {
                    "Unknown Artist"
                },

            color =
                Color(0xFFAAAAAA),

            fontSize =
                15.sp
        )


        Text(

            text =
                song.album.ifBlank {
                    "Unknown Album"
                },

            color =
                Color(0xFF777777),

            fontSize =
                12.sp
        )


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        Slider(

            value =
                if (duration > 0) {

                    currentPosition
                        .toFloat()
                        .coerceIn(
                            0f,
                            duration.toFloat()
                        )

                } else {

                    0f
                },

            onValueChange = {

                service?.seekTo(
                    it.toInt()
                )
            },

            valueRange =
                0f..maxOf(
                    duration.toFloat(),
                    1f
                ),

            modifier =
                Modifier.fillMaxWidth()
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(

                text =
                    formatTime(
                        currentPosition
                    ),

                color =
                    Color.Gray,

                fontSize =
                    11.sp
            )


            Text(

                text =
                    formatTime(
                        duration
                    ),

                color =
                    Color.Gray,

                fontSize =
                    11.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceEvenly,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onShuffle
            ) {

                Icon(

                    Icons.Default.Shuffle,

                    contentDescription =
                        "Shuffle",

                    tint =
                        if (shuffleEnabled)
                            Color.White
                        else
                            Color(0xFF666666)
                )
            }


            IconButton(
                onClick = {
                    service?.playPrevious()
                }
            ) {

                Icon(

                    Icons.Default.SkipPrevious,

                    contentDescription =
                        "Previous",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(40.dp)
                )
            }


            IconButton(

                onClick = {

                    if (playing) {

                        service?.pause()

                    } else {

                        service?.resume()
                    }
                },

                modifier =
                    Modifier
                        .size(70.dp)
                        .clip(
                            CircleShape
                        )
                        .background(
                            Color.White
                        )
            ) {

                Icon(

                    imageVector =
                        if (playing)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,

                    contentDescription =
                        "Play",

                    tint =
                        Color.Black,

                    modifier =
                        Modifier.size(40.dp)
                )
            }


            IconButton(
                onClick = {
                    service?.playNext()
                }
            ) {

                Icon(

                    Icons.Default.SkipNext,

                    contentDescription =
                        "Next",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(40.dp)
                )
            }


            IconButton(
                onClick =
                    onRepeat
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(

                        Icons.Default.Repeat,

                        contentDescription =
                            "Repeat",

                        tint =
                            if (repeatMode != 0)
                                Color.White
                            else
                                Color(0xFF666666)
                    )


                    Text(

                        text =
                            when (repeatMode) {

                                1 -> "ALL"

                                2 -> "ONE"

                                else -> ""
                            },

                        color =
                            Color.White,

                        fontSize =
                            7.sp
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        Row(

            horizontalArrangement =
                Arrangement.Center
        ) {

            Icon(

                Icons.Default.MusicNote,

                contentDescription =
                    null,

                tint =
                    Color(0xFF444444),

                modifier =
                    Modifier.size(16.dp)
            )


            Spacer(
                modifier =
                    Modifier.width(5.dp)
            )


            Text(

                text =
                    "A10S MUSIC PLAYER",

                color =
                    Color(0xFF444444),

                fontSize =
                    9.sp,

                letterSpacing =
                    1.5.sp
            )
        }
    }
}


// ============================================================================
// TIME
// ============================================================================

fun formatTime(
    milliseconds: Int
): String {

    val totalSeconds =
        milliseconds / 1000

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60


    return String.format(
        "%d:%02d",
        minutes,
        seconds
    )
}


fun formatTimeLong(
    milliseconds: Long
): String {

    val totalSeconds =
        milliseconds / 1000

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60


    return String.format(
        "%d:%02d",
        minutes,
        seconds
    )
}