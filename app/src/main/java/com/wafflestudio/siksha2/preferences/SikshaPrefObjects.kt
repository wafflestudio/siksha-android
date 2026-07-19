package com.wafflestudio.siksha2.preferences

import android.content.SharedPreferences
import com.wafflestudio.siksha2.models.RestaurantOrder
import com.wafflestudio.siksha2.network.OAuthProvider
import com.wafflestudio.siksha2.preferences.serializer.Serializer
import com.wafflestudio.siksha2.ui.main.restaurant.MenuFilterCondition
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

    val hiddenRestaurantsOrder: Preference<RestaurantOrder> =
        Preference(
            "hiddenRestaurantsOrder",
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

    // TODO: nullable preference 만들어서 초기값 null 로 두기, 임시로 kakao 로 설정
    val oAuthProvider: Preference<OAuthProvider> =
        Preference(
            "oAuthProvider",
            OAuthProvider.KAKAO,
            sharedPreferences,
            serializer,
            OAuthProvider::class.java
        )

    val communityIsAnonymous: Preference<Boolean> =
        Preference(
            "communityIsAnonymous",
            true,
            sharedPreferences,
            serializer,
            Boolean::class.java
        )

    val menuFilterCondition: Preference<MenuFilterCondition> =
        Preference(
            "menuFilterCondition",
            MenuFilterCondition(
                distance = 1000f,
                minPrice = 2500f,
                maxPrice = 10000f,
                isOpen = false,
                favorite = false,
                hasReview = false,
                minRating = 0f,
                categories = emptySet()
            ),
            sharedPreferences,
            serializer,
            MenuFilterCondition::class.java
        )

    val favoriteModalShown: Preference<Boolean> =
        Preference(
            "favoriteModalShown",
            false,
            sharedPreferences,
            serializer,
            Boolean::class.java
        )

    val favoriteTooltipShown: Preference<Boolean> =
        Preference(
            "favoriteTooltipShown",
            false,
            sharedPreferences,
            serializer,
            Boolean::class.java
        )

    val fcmToken: Preference<String> =
        Preference(
            "fcmToken",
            "",
            sharedPreferences,
            serializer,
            String::class.java
        )

    val lastRegisteredFcmToken: Preference<String> =
        Preference(
            "lastRegisteredFcmToken",
            "",
            sharedPreferences,
            serializer,
            String::class.java
        )

    val alarmEnabled: Preference<Boolean> =
        Preference(
            "alarmEnabled",
            false,
            sharedPreferences,
            serializer,
            Boolean::class.java
        )
}
