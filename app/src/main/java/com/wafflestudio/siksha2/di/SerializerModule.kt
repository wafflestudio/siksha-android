package com.wafflestudio.siksha2.di

import com.wafflestudio.siksha2.preferences.serializer.MoshiSerializer
import com.wafflestudio.siksha2.preferences.serializer.Serializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class SerializerModule {
    @Binds
    abstract fun bindSerializer(moshiSerializer: MoshiSerializer): Serializer
}
