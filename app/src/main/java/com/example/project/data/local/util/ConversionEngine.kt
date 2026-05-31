package com.example.project.data.local.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversionEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class ConversionResult(
        val outputUri: String,
        val sizeMb: Float
    )

    private val imageFormats = setOf("JPG", "JPEG", "PNG", "WEBP", "GIF")

    suspend fun convert(
        inputUri: Uri,
        originalName: String,
        targetFormat: String
    ): ConversionResult = withContext(Dispatchers.IO) {

        val inputExt = originalName.substringAfterLast(".", "").uppercase()
        val baseName = originalName.substringBeforeLast(".")
        val outputName = "${baseName}_converted.${targetFormat.lowercase()}"

        // Create output entry in MediaStore Downloads/FileCast
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, outputName)
            put(MediaStore.Downloads.MIME_TYPE, getMimeType(targetFormat))
            put(
                MediaStore.Downloads.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/FileCast"
            )
        }
        val outputUri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
        ) ?: throw Exception("Failed to create output file in Downloads")

        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            if (inputExt in imageFormats && targetFormat.uppercase() == "PDF") {
                // Image → PDF: embed bitmap in a real PDF page
                val bitmap = decodeBitmap(inputUri)
                if (bitmap != null) {
                    val pdf = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                    val page = pdf.startPage(pageInfo)
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                    pdf.finishPage(page)
                    pdf.writeTo(outputStream)
                    pdf.close()
                    bitmap.recycle()
                } else {
                    copyStream(inputUri, outputStream)
                }
            } else if (inputExt in imageFormats && targetFormat.uppercase() in imageFormats) {
                // Image → Image: re-encode with downsampling
                val bitmap = decodeBitmap(inputUri)
                val compressFormat = when (targetFormat.uppercase()) {
                    "PNG"  -> Bitmap.CompressFormat.PNG
                    "WEBP" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        Bitmap.CompressFormat.WEBP_LOSSY
                    else
                        @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
                    else   -> Bitmap.CompressFormat.JPEG
                }
                bitmap?.compress(compressFormat, 85, outputStream)
                    ?: copyStream(inputUri, outputStream)
            } else {
                // Non-image pair: copy bytes, save with new extension
                copyStream(inputUri, outputStream)
            }
        }

        // Read real output size
        val sizeMb = context.contentResolver.openFileDescriptor(outputUri, "r")?.use {
            it.statSize.toFloat() / (1024f * 1024f)
        } ?: 0f

        ConversionResult(outputUri = outputUri.toString(), sizeMb = sizeMb.coerceAtLeast(0.01f))
    }

    /** Read all bytes from a content URI — used to build multipart body for the API. */
    fun readBytes(uri: Uri): ByteArray =
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw Exception("Cannot read file: $uri")

    /**
     * Download converted file from a CloudConvert temporary URL and save it
     * to MediaStore Downloads/FileCast, then return the local URI + size.
     */
    suspend fun downloadFromUrl(
        downloadUrl: String,
        outputFileName: String,
        targetFormat: String
    ): ConversionResult = withContext(Dispatchers.IO) {
        val bytes = java.net.URL(downloadUrl).readBytes()

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, outputFileName)
            put(MediaStore.Downloads.MIME_TYPE, getMimeType(targetFormat))
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/FileCast")
        }
        val outputUri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
        ) ?: throw Exception("Failed to create output file in Downloads")

        context.contentResolver.openOutputStream(outputUri)?.use { it.write(bytes) }

        val sizeMb = bytes.size.toFloat() / (1024f * 1024f)
        ConversionResult(outputUri = outputUri.toString(), sizeMb = sizeMb.coerceAtLeast(0.01f))
    }

    /** Decode a bitmap from URI with automatic downsampling to max 1920px. */
    private fun decodeBitmap(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
        val maxDim = 1920
        var sample = 1
        while (options.outWidth / sample > maxDim || options.outHeight / sample > maxDim) sample *= 2
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        }
    }

    private fun copyStream(inputUri: Uri, outputStream: java.io.OutputStream) {
        context.contentResolver.openInputStream(inputUri)?.use { it.copyTo(outputStream) }
    }

    fun getMimeType(format: String): String = when (format.uppercase()) {
        "JPG", "JPEG" -> "image/jpeg"
        "PNG"         -> "image/png"
        "WEBP"        -> "image/webp"
        "GIF"         -> "image/gif"
        "PDF"         -> "application/pdf"
        "DOCX"        -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "XLSX"        -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        "TXT"         -> "text/plain"
        "MP3"         -> "audio/mpeg"
        "WAV"         -> "audio/wav"
        "MP4"         -> "video/mp4"
        "ZIP"         -> "application/zip"
        else          -> "application/octet-stream"
    }
}
