package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun LeaveReviewKeywordChip(
    keyword: String,
    selected: Boolean,
    onClickKeyword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = keyword,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) SikshaTheme.colors.Orange500 else SikshaTheme.colors.Gray800,
        modifier = modifier.clickable { onClickKeyword() }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(8.dp),
                color = if (selected) SikshaTheme.colors.Orange500 else SikshaTheme.colors.Gray200
            )
            .padding(horizontal = 11.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeaveReviewKeywordChips(
    keywords: List<String>,
    selectedKeyword: String,
    selectKeywordFromList: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        keywords.forEachIndexed { idx, keyword ->
            LeaveReviewKeywordChip(
                keyword = keyword,
                selected = (keyword == selectedKeyword),
                onClickKeyword = { selectKeywordFromList(idx) }
            )
        }
    }
}

@Composable
@Preview
fun LeaveReviewKeywordChipsPreview() {
    LeaveReviewKeywordChips(
        listOf("맛있어요", "또 먹고 싶어요", "어쩌구", "저쩌구"),
        "어쩌구",
        {},
        modifier = Modifier.background(Color.White).width(50.dp)
    )
}
