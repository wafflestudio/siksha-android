package com.wafflestudio.siksha.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wafflestudio.siksha.model.Menu

class MenuPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager, onlyFavorites: Boolean) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
    companion object {
        const val TODAY_INDEX = 0
        const val TOMORROW_INDEX = 1

        const val TODAY_BREAKFAST_INDEX = 0
        const val TODAY_LUNCH_INDEX = 1
        const val TODAY_DINNER_INDEX = 2
        const val TOMORROW_BREAKFAST_INDEX = 3
        const val TOMORROW_LUNCH_INDEX = 4
        const val TOMORROW_DINNER_INDEX = 5
    }

    private val pages = arrayOf(
            MenuFragment.newInstance(true, Menu.Type.BREAKFAST, onlyFavorites),
            MenuFragment.newInstance(true, Menu.Type.LUNCH, onlyFavorites),
            MenuFragment.newInstance(true, Menu.Type.DINNER, onlyFavorites),
            MenuFragment.newInstance(false, Menu.Type.BREAKFAST, onlyFavorites),
            MenuFragment.newInstance(false, Menu.Type.LUNCH, onlyFavorites),
            MenuFragment.newInstance(false, Menu.Type.DINNER, onlyFavorites)
    )

    override fun getItem(position: Int): androidx.fragment.app.Fragment = pages[position]
    override fun getCount(): Int = pages.size
}