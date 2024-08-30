package com.wafflestudio.siksha2

object FeatureChecker {

    enum class Feature {
        COMMUNITY_TAB
    }

    private val featureFlags = mapOf(
        Feature.COMMUNITY_TAB to true
    )

    fun isFeatureEnabled(feature: Feature): Boolean {
        return featureFlags[feature] ?: false
    }
}
