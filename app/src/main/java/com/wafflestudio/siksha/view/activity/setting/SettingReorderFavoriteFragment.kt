package com.wafflestudio.siksha.view.activity.setting

class SettingReorderFavoriteFragment : SettingReorderMainFragment() {

  companion object {
    fun newInstance(): SettingReorderFavoriteFragment = SettingReorderFavoriteFragment()
  }

  override val onlyFavorites: Boolean get() = true
}