package com.wafflestudio.siksha2.compose.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
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
        modifier = Modifier.padding(bottom = 9.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 90.dp)
                .padding(top = 2.dp, bottom = 10.dp)
        ) {
            Column {
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
                                fontSize = dpToSp(12.dp)
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
                        color = SikshaColors.Gray500
                    )
                }
                // TODO: 말풍선 테두리 strokeWidth 말고 shadow로 처리하기
                Text(
                    text = review?.comment ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 80.dp)
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .paint(
                            painterResource(R.drawable.ic_speech_bubble),
                            contentScale = ContentScale.FillBounds
                        )
                        .padding(start = 30.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                    color = SikshaColors.Gray800,
                    fontSize = dpToSp(dp = 12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemReviewPreview() {
    ItemReview(
        review = Review(
            id = 0,
            menuId = 0,
            userId = 1234,
            score = 5.0,
            comment = "그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 ",
            createdAt = "2023-11-29T09:40:10.322Z",
            etc = null
        )
    )
}
