package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

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