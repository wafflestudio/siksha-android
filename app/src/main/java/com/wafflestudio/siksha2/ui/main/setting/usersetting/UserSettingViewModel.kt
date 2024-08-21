package com.wafflestudio.siksha2.ui.main.setting.usersetting

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.User
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
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    init {
        viewModelScope.launch {
            _userData.value = userStatusManager.getUserData()
        }
    }

    fun updateImageUri(uri: Uri) {
        _userData.value?.profileUrl = uri
    }

    suspend fun patchUserData(context: Context, nickname: String) {
        val image = _userData.value?.profileUrl.let {
            getCompressedImage(context, it)
        }?.let { file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
        val updatedUserData = userStatusManager.updateUserProfile(nickname, image)
        _nickname.value = updatedUserData.nickname
    }
}
