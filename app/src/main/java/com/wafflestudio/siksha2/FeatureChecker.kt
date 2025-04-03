package com.wafflestudio.siksha2

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureChecker @Inject constructor() {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 1 else 43200
        }
        remoteConfig.setDefaultsAsync(R.xml.featurechecker_default)
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                // Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys);

//                if (configUpdate.updatedKeys.contains("welcome_message")) {
//                    remoteConfig.activate().addOnCompleteListener {
//                        // displayWelcomeMessage()
//                    }
//                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                // Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })
    }

    fun fetchFeaturesConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Fetch Success: ${remoteConfig.getBoolean("festivalFeatureEnabled")}")
                } else {
                    Timber.d("Fetch Failed: ${remoteConfig.getBoolean("festivalFeatureEnabled")}")
                }
            }
    }

    fun isFeatureEnabled(featureFlag: String): Boolean {
        return remoteConfig.getBoolean(featureFlag)
    }
}
