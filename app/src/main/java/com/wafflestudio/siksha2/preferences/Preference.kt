package com.wafflestudio.siksha2.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.wafflestudio.siksha2.preferences.serializer.Serializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.reflect.Type

// TODO: NullablePreference 추가하기
class Preference<T>(
    private val key: String,
    private val defaultValue: T,
    private val sharedPreferences: SharedPreferences,
    private val serializer: Serializer,
    private val type: Type
) {
    @ExperimentalCoroutinesApi
    private val keyFlow = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            kotlin.runCatching { trySend(key) }.getOrDefault(defaultValue)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun asFlow(): Flow<T> {
        return keyFlow
            .filter { it == key || it == null }
            .onStart { emit("onAttach Trigger") }
            .map { getValue() }
            .conflate()
    }

    fun getValue(): T =
        sharedPreferences.getString(key, null)?.let { serializer.deserialize<T>(it, type) }
            ?: defaultValue

    fun setValue(value: T) {
        sharedPreferences.edit {
            this.putString(key, serializer.serialize(value, type))
            apply()
        }
    }
}
