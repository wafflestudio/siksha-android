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
        // 학생회관 식당 메뉴 10개
        val mockMenus1 = (1..10).map { i ->
            FavoriteMenuDto(
                id = i.toLong(),
                code = "MENU%03d".format(i),
                name_kr = "학생회관 메뉴 $i",
                name_en = "Student Menu $i",
                price = 4000 + i * 300,
                etc = listOf("한식"),
                score = (3..5).random().toDouble(),
                review_cnt = i * 5,
                like_cnt = i * 10,
                is_liked = i % 2 == 0,
                alarm = i % 3 == 0
            )
        }

        val mockRestaurant1 = FavoriteRestaurantDto(
            id = 100,
            code = "REST001",
            name_kr = "학생회관 식당",
            name_en = "Student Cafeteria",
            addr = "서울대학교 1동",
            lat = 37.459,
            lng = 126.951,
            menus = mockMenus1
        )

        // 두레미담 식당 메뉴 15개
        val mockMenus2 = (11..25).map { i ->
            FavoriteMenuDto(
                id = i.toLong(),
                code = "MENU%03d".format(i),
                name_kr = "두레미담 메뉴 $i",
                name_en = "Duremidam Menu $i",
                price = 5000 + i * 200,
                etc = listOf("일식"),
                score = (3..5).random().toDouble(),
                review_cnt = i * 4,
                like_cnt = i * 7,
                is_liked = i % 2 != 0,
                alarm = i % 4 == 0
            )
        }

        val mockRestaurant2 = FavoriteRestaurantDto(
            id = 101,
            code = "REST002",
            name_kr = "두레미담 식당",
            name_en = "Duremidam Cafeteria",
            addr = "서울대학교 2동",
            lat = 37.460,
            lng = 126.952,
            menus = mockMenus2
        )

        _restaurants.value = listOf(mockRestaurant1, mockRestaurant2)
    }
}
