package com.wafflestudio.siksha.util

import android.util.Base64
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Suppress("SpellCheckingInspection")
class SikshaEncoder(private val secret: String, private val moshi: Moshi) {
  companion object {
    private const val ALGORITHM_NAME = "HmacSHA256"
  }

  private fun base64(byteArray: ByteArray): String = Base64.encodeToString(
      byteArray,
      Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE
  )

  fun encode(mealId: Int, score: Float, device: String): String {
    val hs256 = Mac.getInstance(ALGORITHM_NAME)
    val secretKey = SecretKeySpec(secret.toByteArray(), ALGORITHM_NAME)
    hs256.init(secretKey)

    val header = base64(moshi.adapter(Header::class.java).toJson(Header()).toByteArray())
    val payload = base64(moshi.adapter(Payload::class.java).toJson(Payload(mealId, score, device)).toByteArray())
    val signature = base64(hs256.doFinal("$header.$payload".toByteArray()))
    return "$header.$payload.$signature"
  }

  data class Header(
      @field:Json(name = "alg") val algorithm: String = "HS256",
      @field:Json(name = "JWT") val type: String = "JWT"
  )

  data class Payload(
      @field:Json(name = "meal_id") val mealId: Int,
      @field:Json(name = "score") val score: Float,
      @field:Json(name = "device") val device: String,
      @field:Json(name = "device_type") val deviceType: String = "a"
  )
}
