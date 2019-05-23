package com.wafflestudio.siksha.di

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("SpellCheckingInspection")
@Module
class PreferenceModule(private val prefKey: String) {
  @Provides
  @Singleton
  fun provideMoshi(): Moshi {
    return Moshi.Builder().build()
  }

  @Provides
  @Singleton
  fun provideSharedPreference(context: Context): SharedPreferences {
    return context.getSharedPreferences(prefKey, Context.MODE_PRIVATE)
  }
}