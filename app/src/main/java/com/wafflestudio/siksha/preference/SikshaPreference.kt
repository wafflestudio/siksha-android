package com.wafflestudio.siksha.preference

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha.model.MenuResponse
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
        FAVORITE
    }
}