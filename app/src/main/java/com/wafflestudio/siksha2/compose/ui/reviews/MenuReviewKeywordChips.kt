package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun MenuReviewKeywordChip(
    keyword: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            color = SikshaTheme.colors.ElementChip,
            shape = RoundedCornerShape(4.dp)
        )
            .padding(4.dp)
    ) {
        Text(
            text = keyword,
            color = SikshaTheme.colors.Gray700,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MenuReviewKeywordChips(
    keywords: List<String>,
    modifier: Modifier = Modifier
) {
    // TODO: 스크롤 혹은 줄바꿈
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (keyword in keywords) {
            MenuReviewKeywordChip(keyword)
        }
    }
}
