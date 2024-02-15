package com.wafflestudio.siksha2.components.compose.menuDetail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.NanumSquareFontFamily
import com.wafflestudio.siksha2.models.Etc
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.utils.dpToSp
import com.wafflestudio.siksha2.utils.toKoreanDate
import com.wafflestudio.siksha2.utils.toLocalDateTime

@Composable
fun ItemReview(
    review: Review?,
    modifier: Modifier = Modifier,
    showImage: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SikshaColors.White900)
            .padding(bottom = 10.dp)
            .defaultMinSize(minHeight = 90.dp)
            .padding(top = 2.dp, bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Image(
                    modifier = Modifier.padding(bottom = 2.dp),
                    painter = painterResource(R.drawable.ic_review_profile),
                    contentDescription = "profilePic"
                )
                Spacer(modifier = Modifier.width(7.dp))
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "ID" + (review?.userId ?: ""),
                        color = SikshaColors.Black900,
                        fontSize = dpToSp(12.dp),
                        fontFamily = NanumSquareFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    ItemRatingStars(review?.score?.toFloat() ?: 0.0f)
                }
            }
            Text(
                text = review?.createdAt?.toLocalDateTime()?.toLocalDate()?.toKoreanDate() ?: "-",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 10.dp),
                fontSize = dpToSp(12.dp),
                fontFamily = NanumSquareFontFamily,
                fontWeight = FontWeight.Bold,
                color = SikshaColors.Gray500
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp)
                .padding(horizontal = 16.dp, vertical = 2.dp)
        ) {
            ReviewSpeechBubble(
                modifier = Modifier
                    .fillMaxWidth()
                    .matchParentSize()
            )
            Text(
                text = review?.comment ?: "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 10.dp, top = 13.dp, bottom = 15.dp),
                color = SikshaColors.Gray800,
                fontSize = dpToSp(dp = 12.dp),
                fontFamily = NanumSquareFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
        if (showImage) {
            Row(
                modifier = Modifier
                    .padding(start = 30.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (imageUri in review?.etc?.images ?: listOf()) {
                    ItemReviewImage(
                        imageUri = Uri.parse(imageUri),
                        modifier = Modifier.size(100.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ItemReviewPreview() {
    Column {
        ItemReview(
            review = Review(
                id = 0,
                menuId = 0,
                userId = 1234,
                score = 5.0,
                comment = "그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 ",
                createdAt = "2023-11-29T09:40:10.322Z",
                etc = Etc(
                    images = listOf(
                        "https://postfiles.pstatic.net/MjAyMzExMjZfMjcy/MDAxNzAwOTI3NTczMDY5.c9qVmjXE0nBVZpKukBxB9EB0LytpB5Olc6psLGQdWLQg.-D-zLjlG7bIPYm8XmYzK9-l1vitTZAGcimoHM57QATAg.JPEG.jyurisenpai/20231121203650_1.jpg?type=w773",
                        "https://postfiles.pstatic.net/MjAyMzExMjZfMjU5/MDAxNzAwOTI3MjgxMjAz.f7jMVmS7vYGWKWswaOnnxCjgmaN0qgSt0_2VLB-XZrog.WMa7r-nHh9zw_QXO15bA4ts2NabuiEtQQkM6XYSiA1Ug.JPEG.jyurisenpai/20231118200550_1.jpg?type=w773",
                        "https://postfiles.pstatic.net/MjAyMzExMjZfODAg/MDAxNzAwOTI5MDI4NDE3.kprldbXZmLtHlIh2AFuu9jCeWiXbXeO6pF5OpxpJB3Mg.U8aqpMqPJz4bORV05B8M8oVBF9KXTTJhY1oN17NlkaAg.JPEG.jyurisenpai/20231121211321_1.jpg?type=w773"
                    )
                )
            )
        )
        ItemReview(
            review = Review(
                id = 0,
                menuId = 0,
                userId = 1234,
                score = 3.5,
                comment = "그냥저냥 먹을만해요 ",
                createdAt = "2023-11-29T09:40:10.322Z",
                etc = null
            )
        )
        ItemReview(
            review = Review(
                id = 0,
                menuId = 0,
                userId = 1234,
                score = 1.0,
                comment = "맛없어요\n\n\nㅠ",
                createdAt = "2023-11-29T09:40:10.322Z",
                etc = null
            )
        )
    }
}
