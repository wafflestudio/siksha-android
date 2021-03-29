package com.wafflestudio.siksha2.preferences

import android.content.SharedPreferences
import com.wafflestudio.siksha2.models.RestaurantOrder
import com.wafflestudio.siksha2.preferences.serializer.Serializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SikshaPrefObjects @Inject constructor(
    sharedPreferences: SharedPreferences,
    serializer: Serializer
) {
    val showEmptyRestaurant: Preference<Boolean> =
        Preference("showEmptyRestaurant", false, sharedPreferences, serializer, Boolean::class.java)

    val restaurantsOrder: Preference<RestaurantOrder> =
        Preference(
            "restaurantsOrder",
            RestaurantOrder(listOf()),
            sharedPreferences,
            serializer,
            RestaurantOrder::class.java
        )

    val favoriteRestaurantsOrder: Preference<RestaurantOrder> =
        Preference(
            "favoriteRestaurantsOrder",
            RestaurantOrder(listOf()),
            sharedPreferences,
            serializer,
            RestaurantOrder::class.java
        )

    val accessToken: Preference<String> =
        Preference(
            "accessToken",
            "",
            sharedPreferences,
            serializer,
            String::class.java
        )
}
