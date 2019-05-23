package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.FavoriteFragment
import com.wafflestudio.siksha.view.MainFragment
import com.wafflestudio.siksha.view.SettingFragment
import com.wafflestudio.siksha.view.SettingReorderFavoriteFragment
import com.wafflestudio.siksha.view.SettingReorderMainFragment
import com.wafflestudio.siksha.view.SettingVersionFragment
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