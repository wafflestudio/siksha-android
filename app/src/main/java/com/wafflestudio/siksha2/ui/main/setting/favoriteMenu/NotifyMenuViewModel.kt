package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.network.dto.FavoriteMenuDto
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.FavoriteMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotifyMenuViewModel @Inject constructor(
    private val repository: FavoriteMenuRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<NotifyMenuGroupUiModel>>(emptyList())
    val groups: StateFlow<List<NotifyMenuGroupUiModel>> = _groups

    fun loadMenus(token: String) {
        viewModelScope.launch {
            when (val result = repository.getFavoriteMenus(token)) {
                is NetworkResult.Success -> {
                    val uiModels = result.body.result.map { restaurant ->
                        NotifyMenuGroupUiModel(
                            restaurantId = restaurant.id,
                            restaurantName = restaurant.name_kr,
                            menus = restaurant.menus.map { menu ->
                                NotifyMenuUiModel(
                                    id = menu.id,
                                    title = menu.name_kr,
                                    alarm = menu.alarm ?: false,
                                    isChecked = menu.alarm ?: false
                                )
                            }
                        )
                    }
                    _groups.value = uiModels
                }
                else -> _groups.value = emptyList()
            }
        }
    }

    fun loadMockData() {
        val mockRestaurants = (1..4).map { r ->
            NotifyMenuGroupUiModel(
                restaurantId = r.toLong(),
                restaurantName = "테스트 식당 $r",
                menus = (1..10).map { i ->
                    NotifyMenuUiModel(
                        id = (r * 10 + i).toLong(),
                        title = "알림용 메뉴 $r-$i",
                        alarm = i % 2 == 0,
                        isChecked = i % 3 == 0
                    )
                }
            )
        }
        _groups.value = mockRestaurants
    }

    fun onMenuChecked(menuId: Long, isChecked: Boolean) {
        _groups.value = _groups.value.map { group ->
            group.copy(
                menus = group.menus.map { menu ->
                    if (menu.id == menuId) menu.copy(isChecked = isChecked) else menu
                }
            )
        }
    }

    fun saveAlarms(token: String) {
        viewModelScope.launch {
            _groups.value.forEach { group ->
                group.menus.forEach { menu ->
                    if (menu.isChecked != menu.alarm) {
                        if (menu.isChecked) {
                            repository.enableAlarm(token, menu.id)
                        } else {
                            repository.disableAlarm(token, menu.id)
                        }
                    }
                }
            }
        }
    }
}


