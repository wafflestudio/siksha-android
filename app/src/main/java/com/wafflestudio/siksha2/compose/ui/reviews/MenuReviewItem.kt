package com.wafflestudio.siksha2.compose.ui.reviews

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.menudetail.MenuRatingStars
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.utils.toLocalDateTime
import com.wafflestudio.siksha2.utils.toParsedTimeString

@Composable
fun MenuReviewItem(
    userName: String,
    menuRating: Float,
    timeText: String,
    reviewText: String?,
    isLiked: Boolean,
    likeCount: Long,
    modifier: Modifier = Modifier,
    onToggleLike: () -> Unit = {},
    keywords: List<String> = listOf(),
    imageUris: List<Uri> = listOf()
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MenuReviewHeader(userName, menuRating, timeText)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            if (reviewText != null) {
                MenuReviewTextBox(
                    reviewText = reviewText,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.width(10.dp))
            MenuReviewLikeButton(isLiked, likeCount.toInt(), modifier, onToggleLike)
        }

        if (keywords.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            MenuReviewKeywordChips(
                keywords,
                modifier = Modifier.padding(start = 30.dp)
            )
        }
        if (imageUris.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            MenuReviewImages(
                imageUris,
                modifier = Modifier.padding(start = 30.dp)
            )
        }
    }
}

@Composable
fun MenuReviewHeader(
    userName: String,
    menuRating: Float,
    timeText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(R.drawable.ic_review_profile),
            contentDescription = null,
            modifier = Modifier.padding(top = 1.dp).size(32.dp)
        )
        Spacer(Modifier.width(7.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SikshaTheme.colors.Black
            )
            MenuRatingStars(
                initialRating = menuRating,
                modifier = Modifier.width(61.dp).height(10.dp)
            )
        }
        Text(
            text = timeText.toLocalDateTime().toParsedTimeString(),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = SikshaTheme.colors.Gray600
        )
    }
}

@Composable
@Preview
fun MenuReviewHeaderPreview() {
    MenuReviewHeader(
        userName = "ID 1234",
        menuRating = 5f,
        timeText = "2시간 전"
    )
}

@Composable
@Preview
fun MenuReviewItemPreview() {
    Column(
        modifier = Modifier.background(SikshaTheme.colors.BackgroundPrimary)
    ) {
        MenuReviewItem(
            userName = "고추장 찌개",
            menuRating = 4.5f,
            timeText = "2024년 10월 29일",
            reviewText = "그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요",
            isLiked = false,
            likeCount = 12,
            keywords = listOf("또 먹고 싶어요", "가성비 좋아요", "조화로워요")
        )
        Spacer(Modifier.height(10.dp))
        MenuReviewItem(
            userName = "ID 1234",
            menuRating = 5f,
            timeText = "3시간 전",
            reviewText = "가성비 좋아요",
            isLiked = true,
            likeCount = 5
        )
    }
}
