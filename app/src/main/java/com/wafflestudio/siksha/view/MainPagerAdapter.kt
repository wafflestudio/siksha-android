package com.wafflestudio.siksha.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        const val FAVORITE_INDEX = 0
        const val MENU_INDEX = 1
        const val SETTING_INDEX = 2
    }

    private val pages = arrayOf(
            FavoriteFragment.newInstance(),
            MenuFragment.newInstance(),
            SettingFragment.newInstance()
    )

    override fun getItem(position: Int): Fragment = pages[position]

    override fun getCount(): Int = pages.size
}