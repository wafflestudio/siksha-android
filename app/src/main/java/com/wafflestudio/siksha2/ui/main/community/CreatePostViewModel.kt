package com.wafflestudio.siksha2.ui.main.community

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.utils.PathUtil
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CreatePostViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
): ViewModel() {
    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>>
        get() = _imageUriList

    fun createPost(context: Context, content: String) {
        var processedImageList: List<MultipartBody.Part>? = null
        if(_imageUriList.value?.isNotEmpty()==true) {
            // TODO?: state 관리
            val imageList = _imageUriList.value?.map {

            }
        }
    }

    private suspend fun getCompressedImage(context: Context, uri: Uri): MultipartBody.Part {
        val path = PathUtil.getPath(context, uri)
        var file = File(path)
        file = Compressor.compress(context, file) {
            resolution(300, 300)
            size(100000)
            format(Bitmap.CompressFormat.JPEG)
        }
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", file.name, requestBody)
    }
}
