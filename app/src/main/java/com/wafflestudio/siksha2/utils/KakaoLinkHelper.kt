package com.wafflestudio.siksha2.utils

import android.content.Context
import android.widget.Toast
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.share.ShareClient

object KakaoLinkHelper {
    fun shareMenu(context: Context, restaurantName: String, menuData: List<Pair<String, String>>, menuGroupId: Long) {
        val menuText = menuData.joinToString("\n") { "${it.first}: ${it.second}" }

        val webUrl = "https://siksha.wafflestudio.com"

        val feedTemplate = FeedTemplate(
            content = Content(
                title = "오늘의 학식: $restaurantName",
                description = menuText,
                imageUrl = "https://k.kakaocdn.net/dn/b7fPmH/btsKdRwGLxp/VTmPyo75tuDqQGgxCjFYUk/kakaolink40_original.png",
                link = Link(
                    webUrl = webUrl,
                    mobileWebUrl = webUrl
                )
            ),
            buttonTitle = "자세히 보기"
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareDefault(context, feedTemplate) { sharingResult, error ->
                if (error != null) {
                    Toast.makeText(context, "공유에 실패했습니다: ${error.message}", Toast.LENGTH_SHORT).show()
                } else if (sharingResult != null) {
                    context.startActivity(sharingResult.intent)
                }
            }
        } else {
            Toast.makeText(context, "카카오톡이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
