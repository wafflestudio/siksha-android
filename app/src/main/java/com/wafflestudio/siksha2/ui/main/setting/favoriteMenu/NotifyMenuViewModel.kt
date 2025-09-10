package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _menus = MutableStateFlow<List<NotifyMenuUiModel>>(emptyList())
    val menus: StateFlow<List<NotifyMenuUiModel>> = _menus

    fun loadMenus(token: String) {
        viewModelScope.launch {
            when (val result = repository.getFavoriteMenus(token)) {
                is NetworkResult.Success -> {
                    val uiModels = result.body.result.flatMap { restaurant ->
                        restaurant.menus.map { menu ->
                            NotifyMenuUiModel(
                                id = menu.id,
                                title = menu.name_kr,
                                alarm = menu.alarm ?: false,
                                isChecked = menu.alarm ?: false
                            )
                        }
                    }
                    _menus.value = uiModels
                }
                else -> {
                    _menus.value = emptyList()
                }
            }
        }
    }

    fun onMenuChecked(menuId: Long, isChecked: Boolean) {
        _menus.value = _menus.value.map { menu ->
            if (menu.id == menuId) menu.copy(isChecked = isChecked) else menu
        }
    }

    fun saveAlarms(token: String) {
        viewModelScope.launch {
            _menus.value.forEach { menu ->
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

