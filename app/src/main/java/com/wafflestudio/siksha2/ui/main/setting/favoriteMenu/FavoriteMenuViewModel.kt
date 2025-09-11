package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.network.dto.FavoriteMenuDto
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

    fun toggleLike(menuId: Long, currentLiked: Boolean) {
        viewModelScope.launch {
            _restaurants.value = _restaurants.value.map { restaurant ->
                restaurant.copy(
                    menus = restaurant.menus.map { menu ->
                        if (menu.id == menuId) {
                            menu.copy(is_liked = !currentLiked)
                        } else {
                            menu
                        }
                    }
                )
            }
        }
    }

    fun loadMockData() {
        val mockMenus = listOf(
            FavoriteMenuDto(
                id = 1,
                code = "MENU001",
                name_kr = "김치찌개",
                name_en = "Kimchi Stew",
                price = 5000,
                etc = listOf("매움", "국물요리"),
                score = 4,
                review_cnt = 12,
                like_cnt = 30,
                is_liked = true,
                alarm = false
            ),
            FavoriteMenuDto(
                id = 2,
                code = "MENU002",
                name_kr = "불고기덮밥",
                name_en = "Bulgogi Rice",
                price = 6500,
                etc = listOf("고기", "한식"),
                score = 5,
                review_cnt = 20,
                like_cnt = 50,
                is_liked = true,
                alarm = true
            )
        )

        val mockRestaurant = FavoriteRestaurantDto(
            id = 100,
            code = "REST001",
            name_kr = "학생회관 식당",
            name_en = "Student Cafeteria",
            addr = "서울대학교 1동",
            lat = 37.459,
            lng = 126.951,
            menus = mockMenus
        )

        _restaurants.value = listOf(mockRestaurant)
    }



}
