package com.wafflestudio.siksha2.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha2.BuildConfig
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.result.ResultCallAdapterFactory
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.preferences.serializer.Serializer
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(sikshaPrefObjects: SikshaPrefObjects): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        AUTH_TOKEN_HEADER_KEY,
                        sikshaPrefObjects.accessToken.getValue()
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        @ApplicationContext context: Context,
        serializer: Serializer
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(context.getString(R.string.server_base_url))
            .addCallAdapterFactory(ResultCallAdapterFactory(serializer))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): SikshaApi {
        return retrofit.create(SikshaApi::class.java)
    }

    private const val AUTH_TOKEN_HEADER_KEY = "authorization-token"
}
