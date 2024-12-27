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
import timber.log.Timber
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
            viewModelScope.launch {
                when (val response = userStatusManager.getUserData()) {
                    is NetworkResult.Success -> _userData.value = response.body
                    is NetworkResult.Failure -> _settingEvent.emit(SettingEvent.ChangeProfileFailed(response.message))
                    is NetworkResult.NetworkError -> _settingEvent.emit(SettingEvent.ChangeProfileFailed("네트워크 연결이 불안정합니다."))
                    else -> _settingEvent.emit(SettingEvent.ChangeProfileFailed("알 수 없는 오류가 발생했습니다."))
                }
            }
        }
    }

    private suspend fun checkAppVersion() {
        when (val response = userStatusManager.getVersion()) {
            is NetworkResult.Success -> {
                val latestVersionNum = response.body
                _isLatestAppVersion.value = (packageVersion == latestVersionNum)
            }
            else -> { }
        }
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

    private suspend fun getNicknameToUpdate(nickname: String): NetworkResult<String>? {
        val currentNickname = _userData.value?.nickname
        return if (currentNickname == nickname) {
            null
        } else {
            userStatusManager.checkNickname(nickname).map { _ -> nickname }
        }
    }

    private suspend fun getImageToUpdate(context: Context, imageChanged: Boolean): MultipartBody.Part? {
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
        Timber.d("Enter patchUserData")
        viewModelScope.launch {
            if (nickname.isEmpty()) {
                _settingEvent.emit(SettingEvent.ChangeProfileFailed("닉네임 칸이 비어있습니다."))
                return@launch
            }

            val nicknameToUpdate: String?
            when (val nicknameToUpdateResponse = getNicknameToUpdate(nickname)) {
                is NetworkResult.Failure -> {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(nicknameToUpdateResponse.message))
                    return@launch
                }
                is NetworkResult.NetworkError -> {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed("네트워크 연결이 불안정합니다."))
                    return@launch
                }
                is NetworkResult.UnknownError -> {
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed("알 수 없는 오류가 발생했습니다."))
                    return@launch
                }
                is NetworkResult.Success -> { nicknameToUpdate = nicknameToUpdateResponse.body }
                else -> nicknameToUpdate = null
            }
            val imageToUpdate = getImageToUpdate(context, imageChanged)

            if (nicknameToUpdate == null && !imageChanged) {
                _settingEvent.emit(SettingEvent.ChangeProfileFailed("수정 사항이 없습니다."))
                return@launch
            }

            val isDefaultImage = profileUrlCache == null
            when (val response = userStatusManager.updateUserProfile(nicknameToUpdate, isDefaultImage, imageToUpdate)) {
                is NetworkResult.Success -> {
                    _userData.value = response.body
                    _settingEvent.emit(SettingEvent.ChangeProfileSuccess)
                }
                is NetworkResult.Failure -> {
                    Timber.d("Network Failure")
                    _settingEvent.emit(SettingEvent.ChangeProfileFailed(response.message))
                }
                is NetworkResult.NetworkError -> _settingEvent.emit(SettingEvent.ChangeProfileFailed("네트워크 연결이 불안정합니다."))
                else -> _settingEvent.emit(SettingEvent.ChangeProfileFailed("알 수 없는 오류가 발생했습니다."))
            }
        }
    }
}

sealed interface SettingEvent {
    object ChangeProfileSuccess : SettingEvent
    class ChangeProfileFailed(val errorMessage: String) : SettingEvent
}
