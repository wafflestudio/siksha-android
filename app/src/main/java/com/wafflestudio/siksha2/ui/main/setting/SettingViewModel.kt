package com.wafflestudio.siksha2.ui.main.setting

import android.content.Context
import android.net.Uri
import android.widget.Toast
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
    private var imageChanged: Boolean = false

    init {
        viewModelScope.launch {
            _userData.value = userStatusManager.getUserData()
            versionCheck()
        }
    }

    private suspend fun versionCheck() {
        val latestVersionNum = userStatusManager.getVersion()
        _versionCheck.value = (packageVersion == latestVersionNum)
    }

    fun updateImageUri(uri: Uri) {
        _userData.value?.profileUrl = uri.toString()
        imageChanged = true
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

    private suspend fun getImageToUpdate(context: Context): MultipartBody.Part? {
        return if (imageChanged) {
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

    suspend fun patchUserData(context: Context, nickname: String) {
        val nicknameToUpdate = getNicknameToUpdate(nickname)
        val imageToUpdate = getImageToUpdate(context)

        if (nicknameToUpdate == null && imageToUpdate == null) {
            Toast.makeText(context, "수정 사항이 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUserData = userStatusManager.updateUserProfile(nicknameToUpdate, imageToUpdate)
        _userData.value = updatedUserData
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
}
