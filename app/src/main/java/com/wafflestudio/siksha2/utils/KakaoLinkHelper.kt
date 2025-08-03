package com.wafflestudio.siksha2.utils

import android.content.Context
import android.widget.Toast
import com.kakao.sdk.share.ShareClient
import java.time.LocalDate

object KakaoLinkHelper {
    fun shareMenuWithTemplate(
        context: Context,
        menuData: List<Pair<String, String?>>?,
        restaurantName: String,
        shareDate: LocalDate
    ) {
        val templateId: Long = 121216
        val templateArgs = mutableMapOf<String, String>()

        val today = LocalDate.now()
        templateArgs["date"] = if (shareDate == today) {
            "오늘"
        } else {
            "${shareDate.year}-${shareDate.monthValue}-${shareDate.dayOfMonth}"
        }
        templateArgs["restaurant"] = restaurantName

        menuData?.forEachIndexed { index, menu ->
            templateArgs["menu${index + 1}"] = menu.first
            templateArgs["price${index + 1}"] = if (menu.second?.toIntOrNull() != null) {
                "${menu.second}원"
            } else {
                "-"
            }
        }

        Toast.makeText(context, "식단을 공유합니다.", Toast.LENGTH_SHORT).show()

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareCustom(context, templateId, templateArgs) { sharingResult, error ->
                if (error != null) {
                    Toast.makeText(context, "공유에 실패했습니다: ${error.message}", Toast.LENGTH_SHORT).show()
                } else if (sharingResult != null) {
                    context.startActivity(sharingResult.intent)
                }
            }
        } else {
            Toast.makeText(context, "카카오톡이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
