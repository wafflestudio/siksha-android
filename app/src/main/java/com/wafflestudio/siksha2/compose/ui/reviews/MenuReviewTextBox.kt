package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun MenuReviewTextBox(
    reviewText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.review_speech_bubble_tail),
            contentDescription = null,
            modifier = Modifier.shadow(
                elevation = 3.dp,
                ambientColor = Color(0, 0, 0, 0),
                spotColor = Color(0, 0, 0, 0)
            )
        )
        Box(
            modifier = Modifier.padding(start = 13.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(8.dp),
                    clip = true
                )
                .background(
                    color = SikshaTheme.colors.BackgroundSecondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = reviewText,
                color = SikshaTheme.colors.Black,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.TopStart),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
@Preview
fun MenuReviewTextBoxPreview() {
    MenuReviewTextBox(
        reviewText = "그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 "
    )
}
