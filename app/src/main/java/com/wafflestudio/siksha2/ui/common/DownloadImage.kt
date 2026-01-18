package com.wafflestudio.siksha2.ui.common

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

suspend fun downloadImageToFile(
    context: Context,
    imageUrl: String
): File? = withContext(Dispatchers.IO) {
    return@withContext try {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            return@withContext null
        }

        val input = connection.inputStream
        val tempFile = File.createTempFile("review_img_", ".jpg", context.cacheDir)

        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }

        tempFile
    } catch (e: Exception) {
        null
    }
}
