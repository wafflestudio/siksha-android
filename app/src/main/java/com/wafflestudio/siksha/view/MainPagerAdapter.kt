package com.wafflestudio.siksha.view

class MainPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager)
    : androidx.fragment.app.FragmentPagerAdapter(fragmentManager)
{

    companion object {
        const val FAVORITE_INDEX = 0
        const val MAIN_INDEX = 1
        const val SETTING_INDEX = 2
    }

    private val pages = listOf(
            FavoriteFragment.newInstance(),
            MainFragment.newInstance(),
            SettingWrapperFragment.newInstance()
    )

    override fun getItem(position: Int): androidx.fragment.app.Fragment = pages[position]

    override fun getCount(): Int = pages.size
}