package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.activity.favourite.FavoriteFragment
import com.wafflestudio.siksha.view.activity.main.MainFragment
import com.wafflestudio.siksha.view.activity.setting.SettingFragment
import com.wafflestudio.siksha.view.activity.setting.SettingReorderFavoriteFragment
import com.wafflestudio.siksha.view.activity.setting.SettingReorderMainFragment
import com.wafflestudio.siksha.view.activity.setting.SettingVersionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityFragmentModule {
  @ContributesAndroidInjector(modules = [MainFragmentFragmentModule::class])
  abstract fun mainFragment(): MainFragment

  @ContributesAndroidInjector(modules = [FavoriteFragmentFragmentModule::class])
  abstract fun favoriteFragment(): FavoriteFragment

  @ContributesAndroidInjector
  abstract fun settingFragment(): SettingFragment

  @ContributesAndroidInjector
  abstract fun settingVersionFragment(): SettingVersionFragment

  @ContributesAndroidInjector
  abstract fun settingReorderFavoriteFragment(): SettingReorderFavoriteFragment

  @ContributesAndroidInjector
  abstract fun settingReorderMainFragment(): SettingReorderMainFragment
}