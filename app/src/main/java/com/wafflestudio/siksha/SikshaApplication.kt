package com.wafflestudio.siksha

import com.facebook.stetho.Stetho
import com.wafflestudio.siksha.di.DaggerAppComponent
import com.wafflestudio.siksha.di.EncoderModule
import com.wafflestudio.siksha.di.PreferenceModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

@Suppress("SpellCheckingInspection")
class SikshaApplication : DaggerApplication() {

  private val appComponent = DaggerAppComponent.builder()
      .application(this)
      .preferenceModule(PreferenceModule(BuildConfig.PREF_KEY))
      .encoderModule(EncoderModule(BuildConfig.JWT_SECRET))
      .build()

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
    return appComponent
  }

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      Stetho.initializeWithDefaults(this)
    }
  }
}