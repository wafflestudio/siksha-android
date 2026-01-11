package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun MenuReviewLikeButton(
    isLiked: Boolean,
    likeCount: Int,
    modifier: Modifier = Modifier,
    onToggleLike: () -> Unit = {}
) {
    Column(
        modifier = modifier.background(
            color = SikshaTheme.colors.Gray50,
            shape = RoundedCornerShape(6.dp)
        )
            .clickable { onToggleLike() }
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                if (isLiked) R.drawable.ic_thumb_filled else R.drawable.ic_thumb_outline
            ),
            colorFilter = ColorFilter.tint(SikshaTheme.colors.Orange500),
            contentDescription = if (isLiked) "좋아요 취소" else "좋아요"
        )
        Text(
            text = likeCount.toString(),
            color = SikshaTheme.colors.Orange500,
            fontSize = 9.sp
        )
    }
}

@Preview
@Composable
fun MenuReviewLikeButtonLikedPreview() {
    MenuReviewLikeButton(
        isLiked = true,
        likeCount = 10
    )
}

@Preview
@Composable
fun MenuReviewLikeButtonNotLikedPreview() {
    MenuReviewLikeButton(
        isLiked = false,
        likeCount = 10
    )
}
