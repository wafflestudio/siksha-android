package com.onemonster.siksha.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.onemonster.siksha.BuildConfig
import com.onemonster.siksha.network.SikshaApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): SikshaApi {
        return retrofit.create(SikshaApi::class.java)
    }
}