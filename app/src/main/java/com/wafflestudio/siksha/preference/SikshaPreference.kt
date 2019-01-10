package com.wafflestudio.siksha.preference

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha.model.*
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

    val favorite: List<String>
        get() = getString(PrefKey.FAVORITE, "")
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }

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
        REVIEWED_DINNER
    }
}