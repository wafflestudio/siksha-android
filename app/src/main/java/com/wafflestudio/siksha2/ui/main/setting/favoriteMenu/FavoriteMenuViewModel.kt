package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.repositories.FavoriteMenuRepository
import com.wafflestudio.siksha2.network.dto.FavoriteRestaurantDto
import com.wafflestudio.siksha2.network.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteMenuViewModel @Inject constructor(
    private val repository: FavoriteMenuRepository
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<FavoriteRestaurantDto>>(emptyList())
    val restaurants: StateFlow<List<FavoriteRestaurantDto>> = _restaurants

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadFavoriteMenus(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getFavoriteMenus(token)) {
                is NetworkResult.Success -> {
                    _restaurants.value = result.body.result
                }
                else -> {
                    _restaurants.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }
}
