package com.wafflestudio.siksha.preference

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha.model.Meal
import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.model.Review
import com.wafflestudio.siksha.model.Reviews
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SikshaPreference @Inject constructor(
        private val sharedPreferences: SharedPreferences,
        private val moshi: Moshi
) {
    var menuResponse: MenuResponse?
        get() = getParcelable<MenuResponse>(PrefKey.MENU)
        set(value) = setParcelable(PrefKey.MENU, value)

    var visibleNoMenu: Boolean
        get() = getBoolean(PrefKey.VISIBLE_NO_MENU, true)
        set(value) = setBoolean(PrefKey.VISIBLE_NO_MENU, value)

    val favorite: List<String>
        get() = getString(PrefKey.FAVORITE, "")
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }

    var latestUpdate: String
        get() = getString(PrefKey.LATEST_UPDATE, "-")
        set(value) = setString(PrefKey.LATEST_UPDATE, value)

    val restaurantCodeList: List<String> =
            listOf("VET-001",
                    "GRD-001",
                    "SCO-001",
                    "SCO-002",
                    "SCO-003",
                    "SCO-004",
                    "SCO-005",
                    "SCO-006",
                    "SCO-007",
                    "SCO-008",
                    "SCO-009",
                    "SCO-010",
                    "SCO-011",
                    "SCO-020",
                    "SCO-012",
                    "SCO-021",
                    "SCO-013")

    fun getNameWithCode(code: String): String =
            when (code) {
                "VET-001" -> "수의대식당"
                "GRD-001" -> "대학원기숙사식당"
                "SCO-001" -> "학생회관 식당"
                "SCO-002" -> "농생대 3식당"
                "SCO-003" -> "919동 기숙사 식당"
                "SCO-004" -> "자하연 식당"
                "SCO-005" -> "302동 식당"
                "SCO-006" -> "동원관 식당"
                "SCO-007" -> "감골 식당"
                "SCO-008" -> "사범대 4식당"
                "SCO-009" -> "두레미담"
                "SCO-010" -> "301동 식당"
                "SCO-011" -> "예술계복합연구동 식당"
                "SCO-020" -> "샤반"
                "SCO-012" -> "공대간이식당"
                "SCO-021" -> "소담마루"
                "SCO-013" -> "220동 식당"
                else -> "등록되지 않은 식당입니다"
            }

    fun getRestaurantPriority(code: String): Int {
        val defaultPriority = when (code) {
            "VET-001" -> 0
            "GRD-001" -> 1
            "SCO-001" -> 2
            "SCO-002" -> 3
            "SCO-003" -> 4
            "SCO-004" -> 5
            "SCO-005" -> 6
            "SCO-006" -> 7
            "SCO-007" -> 8
            "SCO-008" -> 9
            "SCO-009" -> 10
            "SCO-010" -> 11
            "SCO-011" -> 12
            "SCO-020" -> 13
            "SCO-012" -> 14
            "SCO-021" -> 15
            "SCO-013" -> 16
            else -> 100
        }
        return getIntPriority(code, defaultPriority)
    }

    fun getFavoriteRestaurantPriority(code: String): Int {
        val defaultPriority = when (code) {
            "VET-001" -> 0
            "GRD-001" -> 1
            "SCO-001" -> 2
            "SCO-002" -> 3
            "SCO-003" -> 4
            "SCO-004" -> 5
            "SCO-005" -> 6
            "SCO-006" -> 7
            "SCO-007" -> 8
            "SCO-008" -> 9
            "SCO-009" -> 10
            "SCO-010" -> 11
            "SCO-011" -> 12
            "SCO-020" -> 13
            "SCO-012" -> 14
            "SCO-021" -> 15
            "SCO-013" -> 16
            else -> 100
        }
        return getFavoriteIntPriority(code, defaultPriority)
    }

    fun setRestaurantPriority(code: String, value: Int) {
        setIntPriority(code, value)
    }

    fun setFavoriteRestaurantPriority(code: String, value: Int) {
        setFavoriteIntPriority(code, value)
    }

    fun addFavorite(code: String) {
        val newFavorites = favorite
                .toMutableList()
                .plus(code)
                .toSet()
                .toList()
                .joinToString()
        setString(PrefKey.FAVORITE, newFavorites)
    }

    fun removeFavorite(code: String) {
        setString(PrefKey.FAVORITE, favorite.filter { it != code }.joinToString())
    }

    var reviews: List<Review>
        get() = getParcelable<Reviews>(PrefKey.REVIEWS)?.reviews ?: listOf()
        private set(value) = setParcelable(PrefKey.REVIEWS, Reviews(value))

    val reviewedBreakfast: Boolean get() = getBoolean(PrefKey.REVIEWED_BREAKFAST, false)
    val reviewedLunch: Boolean get() = getBoolean(PrefKey.REVIEWED_LUNCH, false)
    val reviewedDinner: Boolean get() = getBoolean(PrefKey.REVIEWED_DINNER, false)

    fun registerReview(meal: Meal, type: Menu.Type, review: Review) {
        menuResponse?.let { m ->
            menuResponse = m.copy(
                    today = m.today.copy(
                            menus = m.today.menus.map { menu ->
                                if (menu.meals.contains(meal)) {
                                    menu.copy(
                                            meals = menu.meals.map {
                                                if (it == meal) {
                                                    it.copy(
                                                            scoreCount = it.scoreCount + 1,
                                                            score = it.score?.let { score ->
                                                                (score * it.scoreCount + review.score) / (it.scoreCount + 1)
                                                            } ?: review.score
                                                    )
                                                } else {
                                                    it
                                                }
                                            }
                                    )
                                } else {
                                    menu
                                }
                            }
                    )
            )
        }
        reviews = reviews.toMutableList().plus(review)
        when (type) {
            Menu.Type.BREAKFAST -> setBoolean(PrefKey.REVIEWED_BREAKFAST, true)
            Menu.Type.LUNCH -> setBoolean(PrefKey.REVIEWED_LUNCH, true)
            Menu.Type.DINNER -> setBoolean(PrefKey.REVIEWED_DINNER, true)
        }
    }

    private fun getString(key: PrefKey, defaultValue: String): String =
            sharedPreferences.getString(key.name, defaultValue) ?: defaultValue

    private fun getStringOrNull(key: PrefKey): String? =
            sharedPreferences.getString(key.name, null)

    private fun setString(key: PrefKey, value: String?) {
        sharedPreferences.edit().apply {
            putString(key.name, value)
            commit()
        }
    }

    private fun getIntPriority(key: String, defaultValue: Int): Int =
            sharedPreferences.getInt(key, defaultValue)

    private fun getFavoriteIntPriority(key: String, defaultValue: Int): Int =
            sharedPreferences.getInt("$key:favorite", defaultValue)

    private fun setIntPriority(key: String, value: Int) {
        sharedPreferences.edit().apply {
            putInt(key, value)
            commit()
        }
    }

    private fun setFavoriteIntPriority(key: String, value: Int) {
        sharedPreferences.edit().apply {
            putInt("$key:favorite", value)
            commit()
        }
    }

    private fun getBoolean(key: PrefKey, defaultValue: Boolean): Boolean =
            sharedPreferences.getBoolean(key.name, defaultValue)

    private fun setBoolean(key: PrefKey, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key.name, value)
            commit()
        }
    }


    private inline fun <reified T> getParcelable(key: PrefKey): T? {
        return getStringOrNull(key)?.let { json ->
            moshi.adapter(T::class.java).fromJson(json)
        }
    }

    private inline fun <reified T> setParcelable(key: PrefKey, value: T) {
        value?.let {
            try {
                setString(key, moshi.adapter(T::class.java).toJson(it))
            } catch (e: Exception) {
                Timber.e(e)
            }
        } ?: setString(key, null)
    }

    private enum class PrefKey {
        MENU,
        FAVORITE,
        REVIEWS,
        REVIEWED_BREAKFAST,
        REVIEWED_LUNCH,
        REVIEWED_DINNER,
        VISIBLE_NO_MENU,
        LATEST_UPDATE
    }
}