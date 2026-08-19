package com.dip.a10swalkman

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore

class MusicService : Service() {

    companion object {

        private const val CHANNEL_ID = "A10S_WALKMAN_MUSIC"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY =
            "com.dip.a10swalkman.PLAY"

        const val ACTION_PAUSE =
            "com.dip.a10swalkman.PAUSE"

        const val ACTION_NEXT =
            "com.dip.a10swalkman.NEXT"

        const val ACTION_PREVIOUS =
            "com.dip.a10swalkman.PREVIOUS"

        const val ACTION_STOP =
            "com.dip.a10swalkman.STOP"
    }

    // ================================================================
    // PLAYER
    // ================================================================

    private var mediaPlayer: MediaPlayer? = null

    private var playlist: List<MusicFile> = emptyList()

    private var currentIndex = -1

    private var currentSong: MusicFile? = null

    private var prepared = false

    private val binder = MusicBinder()

    // ================================================================
    // SHUFFLE / REPEAT
    // ================================================================

    /*
     * 0 = OFF
     * 1 = REPEAT ALL
     * 2 = REPEAT ONE
     */

    private var shuffleEnabled = false

    private var repeatMode = 0

    private var shuffleOrder =
        mutableListOf<Int>()

    private var shufflePosition = -1

    // ================================================================
    // QUEUE
    // ================================================================

    private val playbackQueue =
        mutableListOf<MusicFile>()

    private var queuePosition = -1

    // ================================================================
    // BINDER
    // ================================================================

    inner class MusicBinder : Binder() {

        fun getService(): MusicService {
            return this@MusicService
        }
    }

    // ================================================================
    // CREATE
    // ================================================================

    override fun onCreate() {

        super.onCreate()

        createNotificationChannel()

        startForeground(
            NOTIFICATION_ID,
            createNotification(
                "A10S Walkman",
                "Ready"
            )
        )
    }

    // ================================================================
    // BIND
    // ================================================================

    override fun onBind(
        intent: Intent?
    ): IBinder {

        return binder
    }

    // ================================================================
    // START COMMAND
    // ================================================================

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_PLAY -> resume()

            ACTION_PAUSE -> pause()

            ACTION_NEXT -> playNext()

            ACTION_PREVIOUS -> playPrevious()

            ACTION_STOP -> stopPlayback()
        }

        return START_STICKY
    }

    // ================================================================
    // PLAY SONG
    // ================================================================

    fun playSong(
        song: MusicFile,
        songs: List<MusicFile>
    ) {

        if (song.uri.isBlank()) {
            return
        }

        playlist = songs.toList()

        currentIndex =
            playlist.indexOfFirst {
                it.id == song.id
            }

        if (currentIndex < 0) {
            currentIndex = 0
        }

        currentSong = song

        if (shuffleEnabled) {

            rebuildShuffleOrder()

            shufflePosition =
                shuffleOrder.indexOf(currentIndex)
        }

        releasePlayer()

        prepared = false

        try {

            val player =
                MediaPlayer()

            mediaPlayer = player

            player.setAudioAttributes(

                AudioAttributes.Builder()

                    .setUsage(
                        AudioAttributes.USAGE_MEDIA
                    )

                    .setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    )

                    .build()
            )

            player.setDataSource(
                this,
                Uri.parse(song.uri)
            )

            player.setOnPreparedListener {

                prepared = true

                try {

                    it.start()

                    updateNotification(
                        song.title,
                        song.artist
                    )

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

            player.setOnCompletionListener {

                prepared = false

                handleCompletion()
            }

            player.setOnErrorListener {

                    _,
                    _,
                    _ ->

                prepared = false

                updateNotification(
                    "Playback error",
                    song.title
                )

                releasePlayer()

                true
            }

            player.prepareAsync()

        } catch (e: Exception) {

            e.printStackTrace()

            prepared = false

            releasePlayer()

            updateNotification(
                "Unable to play",
                song.title
            )
        }
    }

    // ================================================================
    // PLAY FROM LIBRARY
    // ================================================================

    fun playSongFromLibrary(
        song: MusicFile,
        songs: List<MusicFile>
    ) {

        clearQueue()

        playSong(
            song,
            songs
        )
    }

    // ================================================================
    // COMPLETION
    // ================================================================

    private fun handleCompletion() {

        // ------------------------------------------------------------
        // REPEAT ONE
        // ------------------------------------------------------------

        if (repeatMode == 2) {

            currentSong?.let {

                playSong(
                    it,
                    playlist
                )
            }

            return
        }

        // ------------------------------------------------------------
        // QUEUE
        // ------------------------------------------------------------

        if (playbackQueue.isNotEmpty()) {

            val nextPosition =
                queuePosition + 1

            if (
                nextPosition <
                playbackQueue.size
            ) {

                queuePosition =
                    nextPosition

                val nextSong =
                    playbackQueue[
                        queuePosition
                    ]

                playSong(
                    nextSong,
                    playlist
                )

                return
            }

            clearQueue()
        }

        // ------------------------------------------------------------
        // NORMAL PLAYLIST
        // ------------------------------------------------------------

        val next =
            getNextIndex()

        if (next == -1) {

            releasePlayer()

            updateNotification(
                "A10S Walkman",
                "Playback finished"
            )

            return
        }

        currentIndex = next

        playSong(
            playlist[currentIndex],
            playlist
        )
    }

    // ================================================================
    // QUEUE
    // ================================================================

    fun addToQueue(
        song: MusicFile
    ) {

        if (
            playbackQueue.none {
                it.id == song.id
            }
        ) {

            playbackQueue.add(song)
        }
    }

    // ================================================================
    // ADD TO QUEUE NEXT
    // ================================================================

    fun addToQueueNext(
        song: MusicFile
    ) {

        if (
            playbackQueue.any {
                it.id == song.id
            }
        ) {
            return
        }

        if (playbackQueue.isEmpty()) {

            playbackQueue.add(song)

            if (queuePosition < 0) {
                queuePosition = 0
            }

            return
        }

        val insertPosition =
            (queuePosition + 1)
                .coerceIn(
                    0,
                    playbackQueue.size
                )

        playbackQueue.add(
            insertPosition,
            song
        )
    }

    // ================================================================
    // REMOVE BY POSITION
    // ================================================================

    fun removeFromQueue(
        position: Int
    ) {

        if (
            position < 0 ||
            position >= playbackQueue.size
        ) {
            return
        }

        playbackQueue.removeAt(position)

        if (
            playbackQueue.isEmpty()
        ) {

            queuePosition = -1

            return
        }

        if (position < queuePosition) {

            queuePosition--

        } else if (
            position == queuePosition
        ) {

            if (
                queuePosition >=
                playbackQueue.size
            ) {

                queuePosition =
                    playbackQueue.lastIndex
            }
        }
    }

    // ================================================================
    // REMOVE BY MUSIC FILE
    //
    // This fixes:
    //
    // Argument type mismatch:
    // MusicFile but Int was expected
    // ================================================================

    fun removeFromQueue(
        song: MusicFile
    ) {

        val position =
            playbackQueue.indexOfFirst {
                it.id == song.id
            }

        if (position >= 0) {

            removeFromQueue(position)
        }
    }

    // ================================================================
    // CLEAR QUEUE
    // ================================================================

    fun clearQueue() {

        playbackQueue.clear()

        queuePosition = -1
    }

    // ================================================================
    // GET QUEUE
    // ================================================================

    fun getQueue(): List<MusicFile> {

        return playbackQueue.toList()
    }

    // ================================================================
    // QUEUE POSITION
    // ================================================================

    fun getQueuePosition(): Int {

        return queuePosition
    }

    // ================================================================
    // QUEUE SIZE
    // ================================================================

    fun getQueueSize(): Int {

        return playbackQueue.size
    }

    // ================================================================
    // PLAY NEXT
    // ================================================================

    fun playNext() {

        if (playlist.isEmpty()) {
            return
        }

        // ------------------------------------------------------------
        // QUEUE
        // ------------------------------------------------------------

        if (
            playbackQueue.isNotEmpty()
        ) {

            val nextPosition =
                queuePosition + 1

            if (
                nextPosition <
                playbackQueue.size
            ) {

                queuePosition =
                    nextPosition

                val song =
                    playbackQueue[
                        queuePosition
                    ]

                playSong(
                    song,
                    playlist
                )

                return
            }

            clearQueue()
        }

        // ------------------------------------------------------------
        // NORMAL
        // ------------------------------------------------------------

        val next =
            getNextIndex()

        if (next == -1) {
            return
        }

        currentIndex = next

        playSong(
            playlist[currentIndex],
            playlist
        )
    }

    // ================================================================
    // PLAY NEXT - MUSIC FILE OVERLOAD
    //
    // This fixes:
    //
    // MainActivity calling:
    //
    // playNext(song)
    // ================================================================

    fun playNext(
        song: MusicFile
    ) {

        if (playlist.isEmpty()) {
            return
        }

        val position =
            playbackQueue.indexOfFirst {
                it.id == song.id
            }

        if (position >= 0) {

            queuePosition = position

            playSong(
                song,
                playlist
            )

            return
        }

        addToQueueNext(song)

        val newPosition =
            playbackQueue.indexOfFirst {
                it.id == song.id
            }

        if (newPosition >= 0) {

            queuePosition = newPosition

            playSong(
                song,
                playlist
            )
        }
    }

    // ================================================================
    // PLAY NEXT - LIST OVERLOAD
    //
    // Supports calls such as:
    //
    // playNext(songs)
    // ================================================================

    fun playNext(
        songs: List<MusicFile>
    ) {

        if (songs.isEmpty()) {
            return
        }

        playlist = songs.toList()

        playNext()
    }

    // ================================================================
    // RESUME
    // ================================================================

    fun resume() {

        val player =
            mediaPlayer ?: return

        if (!prepared) {
            return
        }

        try {

            if (!player.isPlaying) {

                player.start()

                currentSong?.let {

                    updateNotification(
                        it.title,
                        it.artist
                    )
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    // ================================================================
    // PAUSE
    // ================================================================

    fun pause() {

        val player =
            mediaPlayer ?: return

        if (!prepared) {
            return
        }

        try {

            if (player.isPlaying) {

                player.pause()

                currentSong?.let {

                    updateNotification(
                        it.title,
                        "Paused"
                    )
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    // ================================================================
    // TOGGLE
    // ================================================================

    fun togglePlayPause() {

        if (isPlaying()) {

            pause()

        } else {

            resume()
        }
    }

    // ================================================================
    // SHUFFLE
    // ================================================================

    fun toggleShuffle(): Boolean {

        shuffleEnabled =
            !shuffleEnabled

        rebuildShuffleOrder()

        return shuffleEnabled
    }

    fun setShuffle(
        enabled: Boolean
    ) {

        shuffleEnabled =
            enabled

        rebuildShuffleOrder()
    }

    fun isShuffleEnabled(): Boolean {

        return shuffleEnabled
    }

    // ================================================================
    // REBUILD SHUFFLE
    // ================================================================

    private fun rebuildShuffleOrder() {

        if (playlist.isEmpty()) {

            shuffleOrder.clear()

            shufflePosition = -1

            return
        }

        if (!shuffleEnabled) {

            shuffleOrder.clear()

            shufflePosition = -1

            return
        }

        val current =
            currentIndex

        shuffleOrder =
            playlist.indices
                .shuffled()
                .toMutableList()

        if (
            current >= 0 &&
            shuffleOrder.contains(current)
        ) {

            shuffleOrder.remove(current)

            shuffleOrder.add(
                0,
                current
            )
        }

        shufflePosition =
            if (current >= 0) {

                shuffleOrder.indexOf(
                    current
                )

            } else {

                -1
            }
    }

    // ================================================================
    // NEXT INDEX
    // ================================================================

    private fun getNextIndex(): Int {

        if (playlist.isEmpty()) {
            return -1
        }

        // ------------------------------------------------------------
        // SHUFFLE
        // ------------------------------------------------------------

        if (shuffleEnabled) {

            if (
                shuffleOrder.isEmpty()
            ) {

                rebuildShuffleOrder()
            }

            val nextPosition =
                shufflePosition + 1

            if (
                nextPosition <
                shuffleOrder.size
            ) {

                shufflePosition =
                    nextPosition

                return shuffleOrder[
                    shufflePosition
                ]
            }

            if (repeatMode == 1) {

                rebuildShuffleOrder()

                if (
                    shuffleOrder.isNotEmpty()
                ) {

                    shufflePosition = 0

                    return shuffleOrder[0]
                }
            }

            return -1
        }

        // ------------------------------------------------------------
        // NORMAL
        // ------------------------------------------------------------

        val next =
            currentIndex + 1

        if (
            next <
            playlist.size
        ) {

            return next
        }

        // ------------------------------------------------------------
        // REPEAT ALL
        // ------------------------------------------------------------

        if (repeatMode == 1) {

            return 0
        }

        return -1
    }

    // ================================================================
    // PREVIOUS
    // ================================================================

    fun playPrevious() {

        if (playlist.isEmpty()) {
            return
        }

        if (
            getCurrentPosition() > 3000
        ) {

            seekTo(0)

            return
        }

        // ------------------------------------------------------------
        // QUEUE
        // ------------------------------------------------------------

        if (
            playbackQueue.isNotEmpty()
        ) {

            val previousPosition =
                queuePosition - 1

            if (
                previousPosition >= 0
            ) {

                queuePosition =
                    previousPosition

                val song =
                    playbackQueue[
                        queuePosition
                    ]

                playSong(
                    song,
                    playlist
                )

                return
            }
        }

        // ------------------------------------------------------------
        // SHUFFLE
        // ------------------------------------------------------------

        if (shuffleEnabled) {

            val previousPosition =
                shufflePosition - 1

            if (
                previousPosition >= 0
            ) {

                shufflePosition =
                    previousPosition

                currentIndex =
                    shuffleOrder[
                        shufflePosition
                    ]

                playSong(
                    playlist[currentIndex],
                    playlist
                )

                return
            }

            if (repeatMode == 1) {

                shufflePosition =
                    shuffleOrder.lastIndex

                if (
                    shufflePosition >= 0
                ) {

                    currentIndex =
                        shuffleOrder[
                            shufflePosition
                        ]

                    playSong(
                        playlist[currentIndex],
                        playlist
                    )
                }

                return
            }

            seekTo(0)

            return
        }

        // ------------------------------------------------------------
        // NORMAL
        // ------------------------------------------------------------

        currentIndex--

        if (currentIndex < 0) {

            if (repeatMode == 1) {

                currentIndex =
                    playlist.lastIndex

            } else {

                currentIndex = 0

                seekTo(0)

                return
            }
        }

        playSong(
            playlist[currentIndex],
            playlist
        )
    }

    // ================================================================
    // REPEAT
    // ================================================================

    fun cycleRepeatMode(): Int {

        repeatMode++

        if (repeatMode > 2) {
            repeatMode = 0
        }

        return repeatMode
    }

    fun setRepeatMode(
        mode: Int
    ) {

        repeatMode =
            mode.coerceIn(
                0,
                2
            )
    }

    fun getRepeatMode(): Int {

        return repeatMode
    }

    // ================================================================
    // STOP
    // ================================================================

    fun stopPlayback() {

        try {

            mediaPlayer?.stop()

        } catch (_: Exception) {
        }

        releasePlayer()

        currentSong = null

        currentIndex = -1

        clearQueue()

        updateNotification(
            "A10S Walkman",
            "Stopped"
        )
    }

    // ================================================================
    // RELEASE PLAYER
    // ================================================================

    private fun releasePlayer() {

        try {

            mediaPlayer?.reset()

        } catch (_: Exception) {
        }

        try {

            mediaPlayer?.release()

        } catch (_: Exception) {
        }

        mediaPlayer = null

        prepared = false
    }

    // ================================================================
    // IS PLAYING
    // ================================================================

    fun isPlaying(): Boolean {

        return try {

            mediaPlayer?.isPlaying
                ?: false

        } catch (_: Exception) {

            false
        }
    }

    // ================================================================
    // CURRENT SONG
    // ================================================================

    fun getCurrentSong(): MusicFile? {

        return currentSong
    }

    // ================================================================
    // CURRENT INDEX
    // ================================================================

    fun getCurrentIndex(): Int {

        return currentIndex
    }

    // ================================================================
    // PLAYLIST
    // ================================================================

    fun getPlaylist(): List<MusicFile> {

        return playlist.toList()
    }

    // ================================================================
    // CURRENT POSITION
    // ================================================================

    fun getCurrentPosition(): Int {

        if (!prepared) {
            return 0
        }

        return try {

            mediaPlayer?.currentPosition
                ?: 0

        } catch (_: Exception) {

            0
        }
    }

    // ================================================================
    // DURATION
    // ================================================================

    fun getDuration(): Int {

        if (!prepared) {
            return 0
        }

        return try {

            mediaPlayer?.duration
                ?: 0

        } catch (_: Exception) {

            0
        }
    }

    // ================================================================
    // SEEK
    // ================================================================

    fun seekTo(
        position: Int
    ) {

        if (!prepared) {
            return
        }

        try {

            val duration =
                mediaPlayer?.duration
                    ?: return

            val safePosition =
                position.coerceIn(
                    0,
                    duration
                )

            mediaPlayer?.seekTo(
                safePosition
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    // ================================================================
    // LOAD SONGS
    // ================================================================

    fun loadSongs(): List<MusicFile> {

        val songs =
            mutableListOf<MusicFile>()

        val collection =
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection =
            arrayOf(

                MediaStore.Audio.Media._ID,

                MediaStore.Audio.Media.TITLE,

                MediaStore.Audio.Media.ARTIST,

                MediaStore.Audio.Media.ALBUM,

                MediaStore.Audio.Media.ALBUM_ID,

                MediaStore.Audio.Media.DURATION,

                MediaStore.Audio.Media.DATA,

                MediaStore.Audio.Media.MIME_TYPE
            )

        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val sortOrder =
            "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val cursor =
            contentResolver.query(

                collection,

                projection,

                selection,

                null,

                sortOrder
            )

        cursor?.use {

            val idColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media._ID
                )

            val titleColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.TITLE
                )

            val artistColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.ARTIST
                )

            val albumColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.ALBUM
                )

            val albumIdColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.ALBUM_ID
                )

            val durationColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.DURATION
                )

            val dataColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.DATA
                )

            val mimeColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.MIME_TYPE
                )

            while (it.moveToNext()) {

                val id =
                    it.getLong(idColumn)

                val title =
                    it.getString(titleColumn)
                        ?: "Unknown Title"

                val artist =
                    it.getString(artistColumn)
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Unknown Artist"

                val album =
                    it.getString(albumColumn)
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Unknown Album"

                val albumId =
                    it.getLong(albumIdColumn)

                val duration =
                    it.getLong(durationColumn)

                val path =
                    it.getString(dataColumn)
                        ?: ""

                val mimeType =
                    it.getString(mimeColumn)
                        ?: "audio/*"

                val contentUri =
                    Uri.withAppendedPath(
                        collection,
                        id.toString()
                    )

                val genre =
                    getGenreForAudio(id)

                songs.add(

                    MusicFile(

                        id = id,

                        title = title,

                        artist = artist,

                        album = album,

                        albumId = albumId,

                        genre = genre,

                        duration = duration,

                        path = path,

                        uri =
                            contentUri.toString(),

                        mimeType = mimeType
                    )
                )
            }
        }

        return songs
    }

    // ================================================================
    // GENRE
    // ================================================================

    private fun getGenreForAudio(
        audioId: Long
    ): String {

        return try {

            val genreUri =
                MediaStore.Audio.Genres
                    .getContentUriForAudioId(
                        "external",
                        audioId.toInt()
                    )

            val projection =
                arrayOf(
                    MediaStore.Audio.Genres.NAME
                )

            contentResolver.query(

                genreUri,

                projection,

                null,

                null,

                null

            )?.use { cursor ->

                if (cursor.moveToFirst()) {

                    cursor.getString(0)
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: ""

                } else {

                    ""
                }

            } ?: ""

        } catch (_: Exception) {

            ""
        }
    }

    // ================================================================
    // NOTIFICATION CHANNEL
    // ================================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(

                    CHANNEL_ID,

                    "A10S Walkman Music",

                    NotificationManager
                        .IMPORTANCE_LOW
                )

            channel.description =
                "A10S Walkman playback"

            channel.setShowBadge(false)

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    // ================================================================
    // NOTIFICATION
    // ================================================================

    private fun createNotification(
        title: String,
        artist: String
    ): Notification {

        val openIntent =
            Intent(
                this,
                MainActivity::class.java
            )

        val openPendingIntent =
            PendingIntent.getActivity(

                this,

                100,

                openIntent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val previousIntent =
            Intent(
                this,
                MusicService::class.java
            )

        previousIntent.action =
            ACTION_PREVIOUS

        val previousPending =
            PendingIntent.getService(

                this,

                101,

                previousIntent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val playIntent =
            Intent(
                this,
                MusicService::class.java
            )

        playIntent.action =
            ACTION_PLAY

        val playPending =
            PendingIntent.getService(

                this,

                102,

                playIntent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val pauseIntent =
            Intent(
                this,
                MusicService::class.java
            )

        pauseIntent.action =
            ACTION_PAUSE

        val pausePending =
            PendingIntent.getService(

                this,

                103,

                pauseIntent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val nextIntent =
            Intent(
                this,
                MusicService::class.java
            )

        nextIntent.action =
            ACTION_NEXT

        val nextPending =
            PendingIntent.getService(

                this,

                104,

                nextIntent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val stopIntent =
            Intent(
                this,
                MusicService::class.java
            )

        stopIntent.action =
            ACTION_STOP

        val stopPending =
            PendingIntent.getService(

                this,

                105,

                stopIntent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val builder =

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {

                Notification.Builder(
                    this,
                    CHANNEL_ID
                )

            } else {

                @Suppress("DEPRECATION")

                Notification.Builder(this)
            }

        builder

            .setContentTitle(title)

            .setContentText(artist)

            .setSmallIcon(
                android.R.drawable
                    .ic_media_play
            )

            .setContentIntent(
                openPendingIntent
            )

            .setOngoing(true)

            .setOnlyAlertOnce(true)

        builder.addAction(

            Notification.Action.Builder(

                null,

                "Previous",

                previousPending

            ).build()
        )

        if (isPlaying()) {

            builder.addAction(

                Notification.Action.Builder(

                    null,

                    "Pause",

                    pausePending

                ).build()
            )

        } else {

            builder.addAction(

                Notification.Action.Builder(

                    null,

                    "Play",

                    playPending

                ).build()
            )
        }

        builder.addAction(

            Notification.Action.Builder(

                null,

                "Next",

                nextPending

            ).build()
        )

        builder.addAction(

            Notification.Action.Builder(

                null,

                "Stop",

                stopPending

            ).build()
        )

        return builder.build()
    }

    // ================================================================
    // UPDATE NOTIFICATION
    // ================================================================

    private fun updateNotification(
        title: String,
        artist: String
    ) {

        val manager =
            getSystemService(
                NotificationManager::class.java
            )

        manager.notify(

            NOTIFICATION_ID,

            createNotification(
                title,
                artist
            )
        )
    }

    // ================================================================
    // DESTROY
    // ================================================================

    override fun onDestroy() {

        releasePlayer()

        currentSong = null

        playlist = emptyList()

        currentIndex = -1

        shuffleOrder.clear()

        shufflePosition = -1

        clearQueue()

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.N
        ) {

            stopForeground(
                STOP_FOREGROUND_REMOVE
            )

        } else {

            @Suppress("DEPRECATION")

            stopForeground(true)
        }

        super.onDestroy()
    }
}