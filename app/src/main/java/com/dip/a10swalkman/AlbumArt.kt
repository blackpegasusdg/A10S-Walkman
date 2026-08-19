package com.dip.a10swalkman

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

object AlbumArt {

    fun getArtwork(
        context: Context,
        song: MusicFile
    ): Bitmap? {

        val retriever = MediaMetadataRetriever()

        return try {

            // First try the file path
            if (song.path.isNotBlank()) {

                try {
                    retriever.setDataSource(song.path)

                    val artwork =
                        retriever.embeddedPicture

                    if (artwork != null) {

                        return android.graphics.BitmapFactory
                            .decodeByteArray(
                                artwork,
                                0,
                                artwork.size
                            )
                    }

                } catch (_: Exception) {
                    // Try URI below
                }
            }

            // Try content URI
            if (song.uri.isNotBlank()) {

                retriever.release()

                val secondRetriever =
                    MediaMetadataRetriever()

                try {

                    secondRetriever.setDataSource(
                        context,
                        Uri.parse(song.uri)
                    )

                    val artwork =
                        secondRetriever.embeddedPicture

                    if (artwork != null) {

                        return android.graphics.BitmapFactory
                            .decodeByteArray(
                                artwork,
                                0,
                                artwork.size
                            )
                    }

                } finally {

                    secondRetriever.release()
                }
            }

            null

        } catch (_: Exception) {

            null

        } finally {

            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }
    }
}