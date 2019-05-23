package com.wafflestudio.siksha.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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