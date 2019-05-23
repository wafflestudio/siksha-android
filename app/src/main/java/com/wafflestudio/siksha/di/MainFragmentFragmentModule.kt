package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.activity.menu.MenuFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainFragmentFragmentModule {
  @ContributesAndroidInjector
  abstract fun menuFragment(): MenuFragment
}