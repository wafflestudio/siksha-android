package com.wafflestudio.siksha.di

import com.squareup.moshi.Moshi
import com.wafflestudio.siksha.util.SikshaEncoder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("SpellCheckingInspection")
@Module
class EncoderModule(private val secret: String) {
    @Provides
    @Singleton
    fun provideSikshaEncoder(moshi: Moshi): SikshaEncoder = SikshaEncoder(secret, moshi)
}