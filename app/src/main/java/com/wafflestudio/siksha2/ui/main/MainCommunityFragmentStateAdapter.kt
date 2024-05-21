package com.wafflestudio.siksha2.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wafflestudio.siksha2.ui.main.community.PostListFragment
import com.wafflestudio.siksha2.ui.main.restaurant.DailyRestaurantFragment
import com.wafflestudio.siksha2.ui.main.setting.SettingFragment

class MainCommunityFragmentStateAdapter(mainFragment: MainFragment) : FragmentStateAdapter(mainFragment) {

    override fun getItemCount(): Int {
        return MainTabState.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            MainTabState.FAVORITE.ordinal -> DailyRestaurantFragment.newInstance(true)
            MainTabState.MAIN.ordinal -> DailyRestaurantFragment.newInstance(false)
            MainTabState.COMMUNITY.ordinal -> PostListFragment()
            MainTabState.SETTINGS.ordinal -> SettingFragment()
            else -> throw IllegalStateException("no such fragment with position $position")
        }
    }
}
