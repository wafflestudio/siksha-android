package com.wafflestudio.siksha2.compose.ui.menudetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaTheme
import java.util.Locale

@Composable
fun MenuRatingsInfo(
    rating: Float,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.defaultMinSize(minWidth = 113.dp, minHeight = 120.dp)
            .border(width = 1.dp, shape = RoundedCornerShape(16.dp), color = SikshaTheme.colors.Gray200)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format(Locale.getDefault(), "%.1f", rating),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = SikshaTheme.colors.Black
        )
        MenuRatingStars(
            initialRating = rating,
            width = 72.dp,
            height = 12.dp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.menu_detail_review_prefix) + " " + reviewCount.toString() + stringResource(R.string.menu_detail_review_suffix),
            fontSize = 14.sp,
            color = SikshaTheme.colors.Black
        )
    }
}

@Composable
@Preview
fun MenuRatingsInfoPreview() {
    MenuRatingsInfo(
        rating = 4.0f,
        reviewCount = 47
    )
}
