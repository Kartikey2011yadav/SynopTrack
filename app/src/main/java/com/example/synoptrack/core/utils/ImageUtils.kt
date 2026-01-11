package com.example.synoptrack.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageUtils {

    private const val MAX_DIMENSION = 800
    private const val COMPRESSION_QUALITY = 75

    suspend fun compressImage(context: Context, uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return@withContext null

            // 1. Resize if needed
            val resizedBitmap = resizeBitmap(originalBitmap, MAX_DIMENSION)

            // 2. Compress to WebP
            val outputStream = ByteArrayOutputStream()
            
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }

            resizedBitmap.compress(format, COMPRESSION_QUALITY, outputStream)
            
            val bytes = outputStream.toByteArray()
            outputStream.close()
            
            // Recycle bitmaps to free memory
            if (originalBitmap != resizedBitmap) originalBitmap.recycle()
            resizedBitmap.recycle()

            return@withContext bytes
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio: Float = width.toFloat() / height.toFloat()
        val finalWidth: Int
        val finalHeight: Int

        if (width > height) {
            finalWidth = maxDimension
            finalHeight = (maxDimension / ratio).toInt()
        } else {
            finalHeight = maxDimension
            finalWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
}
