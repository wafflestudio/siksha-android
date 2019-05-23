package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.MenuFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FavoriteFragmentFragmentModule {
  @ContributesAndroidInjector
  abstract fun menuFragment(): MenuFragment
}