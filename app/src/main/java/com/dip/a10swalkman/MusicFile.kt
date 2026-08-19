package com.dip.a10swalkman

data class MusicFile(

    // MediaStore ID
    val id: Long,

    // Song title
    val title: String,

    // Artist
    val artist: String,

    // Album name
    val album: String = "Unknown Album",

    // Genre
    val genre: String = "Unknown Genre",

    // Song duration in milliseconds
    val duration: Long = 0L,

    // MediaStore album ID
    val albumId: Long = -1L,

    // Actual file path
    val path: String,

    // Content URI used by MediaPlayer
    val uri: String,

    // MIME type of audio file
    val mimeType: String = "audio/*",

    // Embedded metadata
    val embeddedTitle: String? = null,
    val embeddedArtist: String? = null,
    val embeddedAlbum: String? = null,
    val embeddedGenre: String? = null,

    // Embedded artwork
    val hasAlbumArt: Boolean = false
)