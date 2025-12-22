package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.FavoriteMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MenuLoadState {
    object Idle : MenuLoadState
    object Loading : MenuLoadState
    object Loaded : MenuLoadState
}

@HiltViewModel
class NotifyMenuViewModel @Inject constructor(
    private val repository: FavoriteMenuRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<NotifyMenuGroupUiModel>>(emptyList())
    val groups: StateFlow<List<NotifyMenuGroupUiModel>> = _groups

    private val _loadState =
        MutableStateFlow<MenuLoadState>(MenuLoadState.Idle)
    val loadState: StateFlow<MenuLoadState> = _loadState

    private val _alarmEnabled = MutableStateFlow(false)
    val alarmEnabled: StateFlow<Boolean> = _alarmEnabled

    fun setAlarmEnabled(enabled: Boolean) {
        _alarmEnabled.value = enabled
    }

    fun loadMenus(token: String) {
        viewModelScope.launch {
            _loadState.value = MenuLoadState.Loading

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

            _loadState.value = MenuLoadState.Loaded
        }
    }

    fun loadMockData() {
        val mockRestaurants = (1..4).map { r ->
            NotifyMenuGroupUiModel(
                restaurantId = r.toLong(),
                restaurantName = "학생회관 식당",
                menus = (1..10).map { i ->
                    NotifyMenuUiModel(
                        id = (r * 10 + i).toLong(),
                        title = "바지락리조또 & 베사멜소스 띄어쓰기 기준 밑으로 내려오기",
                        alarm = i % 2 == 0,
                        isChecked = i % 3 == 0
                    )
                }
            )
        }
        _groups.value = mockRestaurants
    }

    fun onMenuChecked(menuId: Long, isChecked: Boolean, token: String) {
        Log.d("NotifyMenuViewModel", "Toggle menuId=$menuId | isChecked=$isChecked")

        _groups.value = _groups.value.map { group ->
            group.copy(
                menus = group.menus.map { menu ->
                    if (menu.id == menuId) menu.copy(isChecked = isChecked) else menu
                }
            )
        }

        // 서버에 즉시 반영
        viewModelScope.launch {
            if (isChecked) {
                repository.enableAlarm(token, menuId)
            } else {
                repository.disableAlarm(token, menuId)
            }
        }
    }

    fun disableAllAlarms(token: String) {
        viewModelScope.launch {
            repository.disableAllMenuAlarms(token)
        }
    }
}
