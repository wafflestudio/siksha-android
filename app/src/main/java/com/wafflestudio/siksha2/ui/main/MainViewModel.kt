package com.wafflestudio.siksha2.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.repositories.FavoriteMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val favoriteMenuRepository: FavoriteMenuRepository,
    private val sikshaPrefs: SikshaPrefObjects
) : ViewModel() {

    private var vpState = 1

    fun getVpState() = vpState
    fun setVpState(vpState: Int) {
        this.vpState = vpState
    }

    fun onAlarmPermissionSelected(enabled: Boolean) {
        sikshaPrefs.alarmEnabled.setValue(enabled)

        val token = sikshaPrefs.accessToken.getValue() ?: return

        viewModelScope.launch {
            if (enabled) {
                favoriteMenuRepository.enableAllMenuAlarms(token)
            } else {
                favoriteMenuRepository.disableAllMenuAlarms(token)
            }
        }
    }

    fun markFavoriteModalShown() {
        sikshaPrefs.favoriteModalShown.setValue(true)
    }

    fun shouldShowFavoriteModal(): Boolean {
        return !sikshaPrefs.favoriteModalShown.getValue()
    }
}
