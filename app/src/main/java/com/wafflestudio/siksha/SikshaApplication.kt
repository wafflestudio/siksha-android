package com.wafflestudio.siksha

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import com.wafflestudio.siksha.di.DaggerAppComponent
import com.wafflestudio.siksha.di.EncoderModule
import com.wafflestudio.siksha.di.PreferenceModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

@Suppress("SpellCheckingInspection")
class SikshaApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        DaggerAppComponent.builder()
                .application(this)
                .preferenceModule(PreferenceModule(BuildConfig.PREF_KEY))
                .encoderModule(EncoderModule(BuildConfig.JWT_SECRET))
                .build()
                .inject(this)
    }
}