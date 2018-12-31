package com.onemonster.siksha

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import com.onemonster.siksha.di.DaggerAppComponent
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
        DaggerAppComponent.create().inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }
}