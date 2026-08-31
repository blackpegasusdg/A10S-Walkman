package com.dip.a10swalkman

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

object AlbumArt {

    // 1. High Performance In-Memory LRU Cache
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = (maxMemory / 6).coerceAtLeast(1024 * 32) // ~32-64MB cache

    private val memoryCache = object : LruCache<Long, Bitmap>(cacheSize) {
        override fun sizeOf(key: Long, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    // Set to avoid repeating disk lookups for songs confirmed to have no artwork
    private val missingArtIds = Collections.synchronizedSet(HashSet<Long>())

    fun getCached(songId: Long, minSizePx: Int = 0): Bitmap? {
        val bitmap = memoryCache.get(songId)
        if (bitmap != null) {
            if (minSizePx <= 0 || (bitmap.width >= minSizePx && bitmap.height >= minSizePx)) {
                return bitmap
            }
        }
        return null
    }

    fun isKnownMissing(songId: Long): Boolean {
        return missingArtIds.contains(songId)
    }

    /**
     * Synchronous fallback check (returns cached value immediately, or null without blocking disk)
     */
    fun getArtwork(context: Context, song: MusicFile): Bitmap? {
        return memoryCache.get(song.id)
    }

    /**
     * High-Definition Asynchronous Artwork Loader with Crystal-Clear 32-bit ARGB_8888 decoding
     */
    suspend fun loadArtworkAsync(
        context: Context,
        song: MusicFile,
        targetSizePx: Int = 800
    ): Bitmap? = withContext(Dispatchers.IO) {
        val reqSize = targetSizePx.coerceAtLeast(600)

        // Fast path: memory cache hit if resolution is sufficient
        val cached = memoryCache.get(song.id)
        if (cached != null && (cached.width >= reqSize || cached.width >= 600)) {
            return@withContext cached
        }

        if (missingArtIds.contains(song.id)) return@withContext null

        var bitmap: Bitmap? = null

        // Method 1: Android 10+ Hardware Accelerated High-Res ContentResolver Thumbnail
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && song.uri.isNotBlank()) {
            try {
                val uri = Uri.parse(song.uri)
                bitmap = context.contentResolver.loadThumbnail(
                    uri,
                    Size(reqSize, reqSize),
                    null
                )
            } catch (_: Exception) {
                // Fallback to retriever below
            }
        }

        // Method 2: MediaStore Album Art URI with True Color ARGB_8888
        if (bitmap == null && song.albumId > 0) {
            try {
                val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val albumArtUri = ContentUris.withAppendedId(sArtworkUri, song.albumId)
                context.contentResolver.openInputStream(albumArtUri)?.use { stream ->
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(stream, null, options)

                    val sampleSize = calculateInSampleSize(options, reqSize, reqSize)

                    context.contentResolver.openInputStream(albumArtUri)?.use { stream2 ->
                        val decodeOptions = BitmapFactory.Options().apply {
                            inSampleSize = sampleSize
                            inPreferredConfig = Bitmap.Config.ARGB_8888 // 32-bit vivid crisp color
                        }
                        bitmap = BitmapFactory.decodeStream(stream2, null, decodeOptions)
                    }
                }
            } catch (_: Exception) {
                // Fallback to MediaMetadataRetriever
            }
        }

        // Method 3: MediaMetadataRetriever with Full-Color Decoding
        if (bitmap == null && (song.path.isNotBlank() || song.uri.isNotBlank())) {
            val retriever = MediaMetadataRetriever()
            try {
                if (song.path.isNotBlank()) {
                    retriever.setDataSource(song.path)
                } else {
                    retriever.setDataSource(context, Uri.parse(song.uri))
                }

                val artworkBytes = retriever.embeddedPicture
                if (artworkBytes != null && artworkBytes.isNotEmpty()) {
                    val boundsOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.size, boundsOptions)

                    val sampleSize = calculateInSampleSize(boundsOptions, reqSize, reqSize)

                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = Bitmap.Config.ARGB_8888 // Pristine true color
                    }
                    bitmap = BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.size, decodeOptions)
                }
            } catch (_: Exception) {
                // No artwork found
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) {
                }
            }
        }

        if (bitmap != null) {
            memoryCache.put(song.id, bitmap)
            bitmap
        } else {
            missingArtIds.add(song.id)
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }

    fun clearCache() {
        memoryCache.evictAll()
        missingArtIds.clear()
    }
}