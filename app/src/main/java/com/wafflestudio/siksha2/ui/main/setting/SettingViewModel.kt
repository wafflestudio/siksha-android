package com.wafflestudio.siksha2.ui.main.setting

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.math.pow

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val userStatusManager: UserStatusManager
) : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    init {
        viewModelScope.launch {
            _userData.value = userStatusManager.getUserData()
        }
    }

    var latestVersionNum: Int = 0

    suspend fun versionCheck(packageVersion: String): Boolean {
        val version = userStatusManager.getVersion()
        var currVersionNum = 0

        version.split('.').forEachIndexed { idx, s ->
            latestVersionNum += ((100.0F).pow(2 - idx) * (s.toIntOrNull() ?: 0)).toInt()
        }
        packageVersion.split('.').forEachIndexed { idx, s ->
            currVersionNum = ((100.0F).pow(2 - idx) * (s.toIntOrNull() ?: 0)).toInt()
        }
        return latestVersionNum == currVersionNum
    }

    fun updateImageUri(uri: Uri) {
        _userData.value?.profileUrl = uri.toString()
    }

    suspend fun patchUserData(context: Context, nickname: String) {
        val image = _userData.value?.profileUrl.let {
            val uri = Uri.parse(it)
            getCompressedImage(context, uri)
        }?.let { file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
        val updatedUserData = userStatusManager.updateUserProfile(nickname, image)
        _userData.value = updatedUserData
    }

    val showEmptyRestaurantFlow = restaurantRepository.showEmptyRestaurant.asFlow()

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
