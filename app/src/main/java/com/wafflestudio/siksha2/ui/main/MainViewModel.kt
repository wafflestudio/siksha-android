package com.wafflestudio.siksha2.ui.main

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var tabState = MainTabState.MAIN

    fun getTabState() = tabState
    fun setTabState(tabState: MainTabState) {
        this.tabState = tabState
    }
}
