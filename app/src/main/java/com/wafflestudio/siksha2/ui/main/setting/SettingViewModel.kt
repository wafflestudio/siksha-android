package com.wafflestudio.siksha2.ui.main.setting

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.BuildConfig
import com.wafflestudio.siksha2.models.User
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.utils.ImageUtil.getCompressedImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val userStatusManager: UserStatusManager
) : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    private val _isLatestAppVersion = MutableLiveData<Boolean>()
    val isLatestAppVersion: LiveData<Boolean> get() = _isLatestAppVersion

    val packageVersion: String = BuildConfig.VERSION_NAME

    private var profileUrlCache: String? = _userData.value?.profileUrl

    private val _settingEvent = MutableSharedFlow<SettingEvent>()
    val settingEvent = _settingEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            when (val response = userStatusManager.getUserData()) {
                is NetworkResult.Success -> _userData.value = response.body
                is NetworkResult.Failure ->
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(response.message))
                is NetworkResult.NetworkError ->
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(NETWORK_ERROR_MESSAGE))
                else ->
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(UNKNOWN_ERROR_MESSAGE))
            }
            checkAppVersion()
        }
    }

    private suspend fun checkAppVersion() {
        when (val response = userStatusManager.getVersion()) {
            is NetworkResult.Success -> {
                val version = response.body
                val latestVersion = version.version
                val minVersion = version.minVersion
                if (
                    !isValidVersion(latestVersion) ||
                    !isValidVersion(minVersion) ||
                    !isValidVersion(packageVersion)
                ) {
                    _isLatestAppVersion.value = false
                    return
                }
                val latestVersionCode = versionToLong(latestVersion)
                val minVersionCode = versionToLong(minVersion)
                val packageVersionCode = versionToLong(packageVersion)

                _isLatestAppVersion.value =
                    packageVersionCode in minVersionCode..latestVersionCode
            }
            else -> Unit
        }
    }

    private fun versionToLong(version: String): Long {
        val extractVersion = version.split("-")[0].split(".")

        val major = extractVersion[0].toLongOrNull() ?: 0L
        val minor = extractVersion[1].toLongOrNull() ?: 0L
        val patch = extractVersion[2].toLongOrNull() ?: 0L

        return major * 10000 + minor * 100 + patch
    }

    private fun isValidVersion(version: String): Boolean {
        val verRegex = Regex("^\\d+\\.\\d+\\.\\d+(-rc\\.\\d+)?$")
        return verRegex.matches(version)
    }

    val showEmptyRestaurantFlow = restaurantRepository.showEmptyRestaurant.asFlow()

    fun logoutUser(context: Context, logoutCallBack: () -> Unit) {
        userStatusManager.logoutUser(context, logoutCallBack)
    }

    suspend fun deleteUser(context: Context, withdrawCallback: () -> Unit) {
        userStatusManager.deleteUser(context, withdrawCallback)
    }

    fun toggleShowEmptyRestaurant() {
        restaurantRepository.showEmptyRestaurant.run {
            setValue(getValue().not())
        }
    }

    fun updateImageUri(uri: Uri?) {
        profileUrlCache = uri?.toString()
    }

    fun resetProfileUrlCache() {
        profileUrlCache = _userData.value?.profileUrl
    }

    private suspend fun getNicknameToUpdate(nickname: String): NetworkResult<String>? {
        val currentNickname = _userData.value?.nickname
        return if (currentNickname == nickname) {
            null
        } else {
            userStatusManager.checkNickname(nickname).map { nickname }
        }
    }

    private suspend fun getImageToUpdate(
        context: Context,
        imageChanged: Boolean
    ): MultipartBody.Part? {
        if (!imageChanged || profileUrlCache == null) return null

        return profileUrlCache.let {
            val uri = Uri.parse(it)
            getCompressedImage(context, uri)
        }.let { file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
    }

    fun patchUserData(context: Context, imageChanged: Boolean, nickname: String) {
        viewModelScope.launch {
            if (nickname.isEmpty()) {
                _settingEvent.emit(SettingEvent.ChangeProfileFailed("닉네임 칸이 비어있습니다."))
                return@launch
            }

            val nicknameToUpdate: String?
            when (val response = getNicknameToUpdate(nickname)) {
                is NetworkResult.Failure -> {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(response.message))
                    return@launch
                }
                is NetworkResult.NetworkError -> {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(NETWORK_ERROR_MESSAGE))
                    return@launch
                }
                is NetworkResult.UnknownError -> {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(UNKNOWN_ERROR_MESSAGE))
                    return@launch
                }
                is NetworkResult.Success -> nicknameToUpdate = response.body
                else -> nicknameToUpdate = null
            }
            val imageToUpdate = getImageToUpdate(context, imageChanged)

            if (nicknameToUpdate == null && !imageChanged) {
                _settingEvent.emit(SettingEvent.ChangeProfileFailed("수정 사항이 없습니다."))
                return@launch
            }

            val isDefaultImage = profileUrlCache == null
            when (
                val response = userStatusManager.updateUserProfile(
                    nicknameToUpdate,
                    isDefaultImage,
                    imageToUpdate
                )
            ) {
                is NetworkResult.Success -> {
                    _userData.value = response.body
                    _settingEvent.emit(SettingEvent.ChangeProfileSuccess)
                }
                is NetworkResult.Failure ->
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(response.message))
                is NetworkResult.NetworkError ->
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(NETWORK_ERROR_MESSAGE))
                else ->
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(UNKNOWN_ERROR_MESSAGE))
            }
        }
    }

    private companion object {
        const val NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다."
        const val UNKNOWN_ERROR_MESSAGE = "알 수 없는 오류가 발생했습니다."
    }
}

sealed interface SettingEvent {
    object ChangeProfileSuccess : SettingEvent
    class ChangeProfileFailed(val errorMessage: String) : SettingEvent
}
