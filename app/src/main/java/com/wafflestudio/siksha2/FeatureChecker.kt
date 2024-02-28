package com.wafflestudio.siksha2

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureChecker @Inject constructor() {

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        firebaseRemoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(
                    if (BuildConfig.DEBUG) {
                        0L
                    } else {
                        43200L // 12시간
                    }
                )
                .build()
        )
    }

    suspend fun fetchFeaturesConfig(): Boolean {
        return firebaseRemoteConfig.fetchAndActivate().await()
    }

    fun isFeatureEnabled(featureFlag: String): Boolean {
        return firebaseRemoteConfig.getBoolean(featureFlag)
    }

    fun observeFeatureFlagChanges(featureFlag: String) = callbackFlow {
        trySend(isFeatureEnabled(featureFlag))
        val listener = firebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                firebaseRemoteConfig.activate().addOnCompleteListener {
                    if (configUpdate.updatedKeys.contains(featureFlag)) {
                        trySend(isFeatureEnabled(featureFlag))
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                close(error.cause)
            }
        })
        awaitClose {
            listener.remove()
        }
    }
}
