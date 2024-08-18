package com.wafflestudio.siksha2.ui.main.setting.usersetting

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.utils.ImageUtil.getCompressedImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
        _imageUri.value = image?.let { Uri.parse(it) }
    }

    fun updateImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    suspend fun patchUserData(context: Context, nickname: String) {
        val image = _imageUri.value?.let {
            getCompressedImage(context, it)
        }?.let { file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
        val updatedUserData = userStatusManager.updateUserProfile(nickname, image)
        _nickname.value = updatedUserData.nickname
    }
}
