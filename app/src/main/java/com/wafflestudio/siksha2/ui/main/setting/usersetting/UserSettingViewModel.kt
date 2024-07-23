package com.wafflestudio.siksha2.ui.main.setting.usersetting

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.wafflestudio.siksha2.repositories.UserStatusManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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

    suspend fun startUserSetting(){
        _nickname.value = userStatusManager.getUserNickname()
        val imageList = userStatusManager.getUserImage()
        _imageUri.value = imageList?.firstOrNull()?.let { Uri.parse(it) }
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    private suspend fun compressImage(context: Context, uri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            val bitmap = Glide.with(context).asBitmap().load(uri).submit().get()
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.toByteArray()
        }
    }

    fun sendDataToServer(context: Context) {
        viewModelScope.launch {
            val nickname = _nickname.value ?: return@launch
            val imageUri = _imageUri.value ?: return@launch

            val compressedImage = compressImage(context, imageUri)

            // 서버로 데이터 전송 로직을 여기에 추가하세요.
            // 예시: sendToServer(nickname, compressedImage)
        }
    }
}
