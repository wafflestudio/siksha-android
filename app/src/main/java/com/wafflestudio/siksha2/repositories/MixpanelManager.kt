package com.wafflestudio.siksha2.repositories

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.wafflestudio.siksha2.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixpanelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mp: MixpanelAPI = MixpanelAPI.getInstance(
        context,
        BuildConfig.MIXPANEL_TOKEN,
        false
    )

    fun track(eventName: String, props: JSONObject? = null) {
        if (props != null) {
            mp.track(eventName, props)
        } else {
            mp.track(eventName)
        }
    }
}
