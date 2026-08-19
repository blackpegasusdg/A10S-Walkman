package com.dip.a10swalkman

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore

object MusicRepository {

    fun loadSongs(context: Context): List<MusicFile> {

        val songs = mutableListOf<MusicFile>()

        val collection =
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(

            MediaStore.Audio.Media._ID,

            MediaStore.Audio.Media.TITLE,

            MediaStore.Audio.Media.ARTIST,

            MediaStore.Audio.Media.ALBUM,

            MediaStore.Audio.Media.DURATION,

            MediaStore.Audio.Media.ALBUM_ID,

            MediaStore.Audio.Media.DATA,

            MediaStore.Audio.Media.MIME_TYPE
        )

        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val sortOrder =
            "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val cursor = context.contentResolver.query(
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

            val durationColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.DURATION
                )

            val albumIdColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.ALBUM_ID
                )

            val dataColumn =
                it.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.DATA
                )

            while (it.moveToNext()) {

                val id =
                    it.getLong(idColumn)

                val mediaTitle =
                    it.getString(titleColumn)
                        ?: "Unknown Title"

                val mediaArtist =
                    it.getString(artistColumn)
                        ?: "Unknown Artist"

                val mediaAlbum =
                    it.getString(albumColumn)
                        ?: "Unknown Album"

                val mediaDuration =
                    it.getLong(durationColumn)

                val albumId =
                    it.getLong(albumIdColumn)

                val path =
                    it.getString(dataColumn)
                        ?: ""

                // --------------------------------------------------
                // CONTENT URI
                // --------------------------------------------------

                val contentUri =
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                // --------------------------------------------------
                // READ EMBEDDED FLAC / MP3 METADATA
                // --------------------------------------------------

                val metadata =
                    readEmbeddedMetadata(
                        context,
                        contentUri.toString()
                    )

                val finalTitle =
                    metadata.title
                        ?.takeIf { value ->
                            value.isNotBlank()
                        }
                        ?: mediaTitle

                val finalArtist =
                    metadata.artist
                        ?.takeIf { value ->
                            value.isNotBlank()
                        }
                        ?: mediaArtist

                val finalAlbum =
                    metadata.album
                        ?.takeIf { value ->
                            value.isNotBlank()
                        }
                        ?: mediaAlbum

                val finalGenre =
                    metadata.genre
                        ?.takeIf { value ->
                            value.isNotBlank()
                        }
                        ?: "Unknown Genre"

                val finalDuration =
                    metadata.duration
                        ?.toLongOrNull()
                        ?.takeIf { value ->
                            value > 0
                        }
                        ?: mediaDuration

                songs.add(

                    MusicFile(

                        id = id,

                        title = finalTitle,

                        artist = finalArtist,

                        album = finalAlbum,

                        genre = finalGenre,

                        duration = finalDuration,

                        albumId = albumId,

                        path = path,

                        uri = contentUri.toString(),

                        embeddedTitle =
                            metadata.title,

                        embeddedArtist =
                            metadata.artist,

                        embeddedAlbum =
                            metadata.album,

                        embeddedGenre =
                            metadata.genre,

                        hasAlbumArt =
                            metadata.albumArt != null
                    )
                )
            }
        }

        return songs
    }

    // ============================================================
    // EMBEDDED METADATA
    // ============================================================

    private fun readEmbeddedMetadata(
        context: Context,
        uri: String
    ): EmbeddedMetadata {

        val retriever =
            MediaMetadataRetriever()

        return try {

            retriever.setDataSource(
                context,
                android.net.Uri.parse(uri)
            )

            val title =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_TITLE
                )

            val artist =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_ARTIST
                )

            val album =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_ALBUM
                )

            val genre =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_GENRE
                )

            val duration =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )

            val albumArt =
                retriever.embeddedPicture

            EmbeddedMetadata(

                title = title,

                artist = artist,

                album = album,

                genre = genre,

                duration = duration,

                albumArt = albumArt
            )

        } catch (e: Exception) {

            e.printStackTrace()

            EmbeddedMetadata()

        } finally {

            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }
    }
}

// ================================================================
// EMBEDDED METADATA MODEL
// ================================================================

private data class EmbeddedMetadata(

    val title: String? = null,

    val artist: String? = null,

    val album: String? = null,

    val genre: String? = null,

    val duration: String? = null,

    val albumArt: ByteArray? = null
)