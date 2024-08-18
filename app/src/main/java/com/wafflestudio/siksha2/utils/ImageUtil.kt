package com.wafflestudio.siksha2.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.wafflestudio.siksha2.utils.compressor.Compressor
import com.wafflestudio.siksha2.utils.compressor.format
import com.wafflestudio.siksha2.utils.compressor.resolution
import com.wafflestudio.siksha2.utils.compressor.size
import java.io.File

object ImageUtil {
    suspend fun getCompressedImage(context: Context, imageUri: Uri): File {
        return Compressor.compress(context, imageUri) {
            resolution(300, 300)
            size(100000)
            format(Bitmap.CompressFormat.JPEG)
        }
    }
}
