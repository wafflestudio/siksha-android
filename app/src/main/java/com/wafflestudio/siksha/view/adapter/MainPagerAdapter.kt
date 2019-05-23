package com.wafflestudio.siksha.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wafflestudio.siksha.view.activity.favourite.FavoriteFragment
import com.wafflestudio.siksha.view.activity.main.MainFragment
import com.wafflestudio.siksha.view.activity.setting.SettingWrapperFragment

class MainPagerAdapter(fragmentManager: FragmentManager)
  : FragmentPagerAdapter(fragmentManager) {

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

  override fun getItem(position: Int): Fragment = pages[position]

  override fun getCount(): Int = pages.size
}