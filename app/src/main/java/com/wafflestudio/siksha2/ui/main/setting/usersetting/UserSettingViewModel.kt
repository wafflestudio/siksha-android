package com.wafflestudio.siksha2.ui.main.setting.usersetting

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.utils.PathUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserSettingViewModel @Inject constructor(
    private val userStatusManager: UserStatusManager
) : ViewModel() {
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    init {
        viewModelScope.launch {
            startUserSetting()
        }
    }

    private suspend fun startUserSetting() {
        _nickname.value = userStatusManager.getUserNickname()
        val image = userStatusManager.getUserImage()
        _imageUri.value = image?.firstOrNull()?.let { Uri.parse(it) }
    }

    fun updateImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    suspend fun patchUserData(context: Context, nickname: String) {
        val image = _imageUri.value?.let { getCompressedImage(context, it) }
        val updatedUserData = userStatusManager.updateUserProfile(nickname, image)
        _nickname.value = updatedUserData.nickname
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
