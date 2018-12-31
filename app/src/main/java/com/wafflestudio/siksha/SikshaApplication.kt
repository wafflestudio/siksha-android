package com.wafflestudio.siksha

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import com.wafflestudio.siksha.di.DaggerAppComponent
import com.wafflestudio.siksha.di.PreferenceModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class SikshaApplication : Application(), HasActivityInjector {
    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
        DaggerAppComponent.builder()
                .application(this)
                .preferenceModule(PreferenceModule(BuildConfig.PREF_KEY))
                .build()
                .inject(this)
    }
}