package com.wafflestudio.siksha2.ui.main

enum class MainTabState {
    MAIN,
    COMMUNITY,
    SETTINGS;

    companion object {
        fun fromPosition(position: Int): MainTabState {
            return values()[position]
        }
    }
}
