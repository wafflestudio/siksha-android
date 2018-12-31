package com.wafflestudio.siksha.di

import android.app.Application
import com.wafflestudio.siksha.SikshaApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityModule::class,
    AppModule::class,
    NetworkModule::class,
    PreferenceModule::class])
interface AppComponent : AndroidInjector<SikshaApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun preferenceModule(preferenceModule: PreferenceModule): Builder

        fun build(): AppComponent
    }

    override fun inject(instance: SikshaApplication)
}
