package com.wafflestudio.siksha2.models

import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.network.dto.GetVersionResult

@JsonClass(generateAdapter = true)
class Version(
    var version: String,
    var minVersion: String
) {
    companion object {
        val version = Version("", "")
    }
}

fun GetVersionResult.toVersion() = Version(version = version, minVersion = minVersion)
