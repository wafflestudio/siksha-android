package com.wafflestudio.siksha.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.wafflestudio.siksha.model.Menu

class MenuPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
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
            MenuFragment.newInstance(true, Menu.Type.BREAKFAST),
            MenuFragment.newInstance(true, Menu.Type.LUNCH),
            MenuFragment.newInstance(true, Menu.Type.DINNER),
            MenuFragment.newInstance(false, Menu.Type.BREAKFAST),
            MenuFragment.newInstance(false, Menu.Type.LUNCH),
            MenuFragment.newInstance(false, Menu.Type.DINNER)
    )

    override fun getItem(position: Int): Fragment = pages[position]

    override fun getCount(): Int = pages.size
}