package com.wafflestudio.siksha2.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha2.db.DailyMenusDao
import com.wafflestudio.siksha2.db.RestaurantsDao
import com.wafflestudio.siksha2.db.SikshaDatabase
import com.wafflestudio.siksha2.db.SikshaRoomTypeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, moshi: Moshi): SikshaDatabase {
        return Room.databaseBuilder(context, SikshaDatabase::class.java, "siksha-db")
            .addTypeConverter(SikshaRoomTypeAdapter(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideRestaurantDao(db: SikshaDatabase): RestaurantsDao {
        return db.restaurantDao()
    }

    @Provides
    @Singleton
    fun provideDailyMenuDao(db: SikshaDatabase): DailyMenusDao {
        return db.dailyMenuDao()
    }
}
