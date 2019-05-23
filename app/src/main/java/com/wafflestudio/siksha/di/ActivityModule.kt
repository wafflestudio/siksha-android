package com.wafflestudio.siksha.di

import com.wafflestudio.siksha.view.activity.main.MainActivity
import com.wafflestudio.siksha.view.activity.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {

  @ContributesAndroidInjector
  abstract fun contributeSplashActivity(): SplashActivity

  @ContributesAndroidInjector(modules = [MainActivityFragmentModule::class])
  abstract fun contributeMainActivity(): MainActivity
}