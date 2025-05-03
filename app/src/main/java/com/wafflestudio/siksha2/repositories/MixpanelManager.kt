package com.wafflestudio.siksha2.repositories

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.wafflestudio.siksha2.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixpanelManager @Inject constructor(
    private val context: Context
) {
    private val mp: MixpanelAPI = MixpanelAPI.getInstance(context, context.getString(R.string.mixpanel_token), false)
}
