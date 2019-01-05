package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.MenuFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentFragmentModule {
    @ContributesAndroidInjector
    abstract fun menuFragment(): MenuFragment
}