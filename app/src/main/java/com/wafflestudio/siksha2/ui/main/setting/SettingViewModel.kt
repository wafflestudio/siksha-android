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
import com.wafflestudio.siksha2.utils.showToast
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

    private val _versionCheck = MutableLiveData<Boolean>()
    val versionCheck: LiveData<Boolean> get() = _versionCheck

    val packageVersion: String = BuildConfig.VERSION_NAME
    private val isDefaultImage: Boolean get() = userData.value?.profileUrl == null

    private val _settingEvent = MutableSharedFlow<SettingEvent>()
    val settingEvent = _settingEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            _userData.value = userStatusManager.getUserData()
            checkAppVersion()
        }
    }

    private suspend fun checkAppVersion() {
        val latestVersionNum = userStatusManager.getVersion()
        _versionCheck.value = (packageVersion == latestVersionNum)
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
        _userData.value?.profileUrl = uri?.toString()
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
        return if (isDefaultImage) {
            null
        } else if (imageChanged) {
            _userData.value?.profileUrl.let {
                val uri = Uri.parse(it)
                getCompressedImage(context, uri)
            }?.let { file ->
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestBody)
            }
        } else {
            null
        }
    }

    suspend fun patchUserData(context: Context, imageChanged: Boolean, nickname: String) {
        val nicknameToUpdate = getNicknameToUpdate(nickname)
        val imageToUpdate = getImageToUpdate(context, imageChanged)

        if (nicknameToUpdate == null && !imageChanged) {
            context.showToast("수정 사항이 없습니다.")
            return
        }

        val updatedUserData = userStatusManager.updateUserProfile(nicknameToUpdate, isDefaultImage, imageToUpdate)
        _userData.value = updatedUserData
    }
}

sealed interface SettingEvent {
    object ChangeProfileSuccess: SettingEvent
    object ChangeProfileFailed: SettingEvent
}
