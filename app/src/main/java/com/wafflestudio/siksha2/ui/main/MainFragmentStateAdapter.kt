package com.wafflestudio.siksha2.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wafflestudio.siksha2.ui.main.restaurant.DailyRestaurantFragment
import com.wafflestudio.siksha2.ui.main.setting.SettingFragment

class MainFragmentStateAdapter(mainFragment: MainFragment) : FragmentStateAdapter(mainFragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DailyRestaurantFragment.newInstance(true)
            1 -> DailyRestaurantFragment.newInstance(false)
            2 -> SettingFragment()
            else -> throw IllegalStateException("no such fragment with position $position")
        }
    }
}
