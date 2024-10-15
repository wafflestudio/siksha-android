package com.wafflestudio.siksha2.ui.main.setting

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.BuildConfig
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.models.RestaurantOrder
import com.wafflestudio.siksha2.models.User
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
import retrofit2.HttpException
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

    // profileUrlCache : UserProfile Fragment가 생길 때마다 초기엔 _userData.value?.profileUrl 값으로 초기화, local에서 profile image가 변화가 생기면 저장
    private var profileUrlCache: String? = _userData.value?.profileUrl

    private val _settingEvent = MutableSharedFlow<SettingEvent>()
    val settingEvent = _settingEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            runCatching {
                _userData.value = userStatusManager.getUserData()
                checkAppVersion()
            }.onFailure {
                // TODO: 유저 정보 받아오지 못했을 때 처리 필요
            }
        }
    }

    private suspend fun checkAppVersion() {
        val version = userStatusManager.getVersion()

        val latestVersion = version.version
        val minVersion = version.minVersion

        // TODO: 버전이 잘못된 pattern을 가졌을 때의 처리 (필요한가?)
        if (!isValidVersion(latestVersion) || !isValidVersion(minVersion) || !isValidVersion(packageVersion)) {
            _isLatestAppVersion.value = false
            return
        }

        val latestVersionCode = versionToLong(latestVersion)
        val minVersionCode = versionToLong(minVersion)
        val packageVersionCode = versionToLong(packageVersion)

        _isLatestAppVersion.value = packageVersionCode in minVersionCode..latestVersionCode
    }

    private fun versionToLong(version: String): Long {
        val extractVersion = version.split("-")[0].split(".")

        val major = extractVersion[0].toLongOrNull() ?: 0L
        val minor = extractVersion[1].toLongOrNull() ?: 0L
        val patch = extractVersion[2].toLongOrNull() ?: 0L

        return major * 10000 + minor * 100 + patch
    }

    // Check the version has pattern of 3.1.1 or 2.3.4-rc.1
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

    fun updateOrder(order: RestaurantOrder) {
        restaurantRepository.restaurantsOrder.setValue(order)
    }

    fun updateFavoriteOrder(order: RestaurantOrder) {
        restaurantRepository.favoriteRestaurantsOrder.setValue(order)
    }

    suspend fun getOrderedAllRestaurants(): List<RestaurantInfo> {
        return restaurantRepository.getOrderedRestaurants()
    }

    suspend fun getOrderedFavoriteRestaurants(): List<RestaurantInfo> {
        return restaurantRepository.getOrderedFavoriteRestaurants()
    }

    fun updateImageUri(uri: Uri?) {
        profileUrlCache = uri?.toString()
    }

    fun resetProfileUrlCache() {
        profileUrlCache = _userData.value?.profileUrl
    }

    private suspend fun getNicknameToUpdate(nickname: String): String? {
        val currentNickname = _userData.value?.nickname
        if (currentNickname == nickname) {
            return null
        } else {
            userStatusManager.checkNickname(nickname)
            return nickname
        }
    }

    private suspend fun getImageToUpdate(context: Context, imageChanged: Boolean): MultipartBody.Part? {
        if (!imageChanged || profileUrlCache == null) return null

        return profileUrlCache.let {
            val uri = Uri.parse(it)
            getCompressedImage(context, uri)
        }?.let { file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
    }

    fun patchUserData(context: Context, imageChanged: Boolean, nickname: String) {
        viewModelScope.launch {
            runCatching {
                if (nickname.isEmpty()) {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed("닉네임 칸이 비어있습니다."))
                    return@runCatching
                }

                val nicknameToUpdate = getNicknameToUpdate(nickname)
                val imageToUpdate = getImageToUpdate(context, imageChanged)

                if (nicknameToUpdate == null && !imageChanged) {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed("수정 사항이 없습니다."))
                    return@runCatching
                }

                val isDefaultImage = profileUrlCache == null
                val updatedUserData = userStatusManager.updateUserProfile(nicknameToUpdate, isDefaultImage, imageToUpdate)
                _userData.value = updatedUserData
            }.onFailure {
                when (it) {
                    is HttpException -> {
                        when (it.code()) {
                            409 -> {
                                _settingEvent.emit(SettingEvent.ChangeProfileFailed("이미 존재하는 닉네임입니다."))
                            }

                            else -> {
                                _settingEvent.emit(SettingEvent.ChangeProfileFailed("일시적인 오류가 발생했습니다."))
                            }
                        }
                    }

                    else -> {
                        _settingEvent.emit(SettingEvent.ChangeProfileFailed("일시적인 오류가 발생했습니다."))
                    }
                }
            }.onSuccess {
                _settingEvent.emit(SettingEvent.ChangeProfileSuccess)
            }
        }
    }
}

sealed interface SettingEvent {
    object ChangeProfileSuccess : SettingEvent
    class ChangeProfileFailed(val errorMessage: String) : SettingEvent
}
