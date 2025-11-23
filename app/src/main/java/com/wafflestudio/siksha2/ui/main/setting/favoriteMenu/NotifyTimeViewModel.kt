package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.FavoriteMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotifyTimeViewModel @Inject constructor(
    private val repository: FavoriteMenuRepository
) : ViewModel() {

    private val _alarmType = MutableLiveData<String>()
    val alarmType: LiveData<String> get() = _alarmType

    fun loadAlarmType(token: String) {
        viewModelScope.launch {
            when (val result = repository.fetchAlarmType(token)) {
                is NetworkResult.Success -> {
                    _alarmType.value = result.body.alarm_type
                }
                else -> {
                    _alarmType.value = "DAILY" // default
                }
            }
        }
    }

    fun updateAlarmType(type: String, token: String) {
        viewModelScope.launch {
            repository.setAlarmType(type, token)
        }
    }
}
