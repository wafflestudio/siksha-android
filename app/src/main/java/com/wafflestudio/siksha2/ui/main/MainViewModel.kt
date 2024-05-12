package com.wafflestudio.siksha2.ui.main

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var vpState = 1

    fun getVpState() = vpState
    fun setVpState(vpState: Int) {
        this.vpState = vpState
    }
}
