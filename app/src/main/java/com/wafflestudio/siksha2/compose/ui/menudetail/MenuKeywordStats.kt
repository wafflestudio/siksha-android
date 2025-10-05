package com.wafflestudio.siksha2.compose.ui.menudetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.CancelIcon
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun MenuKeywordStat(
    keywordString: String,
    keywordCount: Long,
    keywordIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.background(color = SikshaTheme.colors.Gray100, shape = RoundedCornerShape(8.dp))
            .padding(top = 6.dp, bottom = 6.dp, start = 14.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        keywordIcon()
        Spacer(Modifier.width(6.dp))
        Text(
            text = keywordString,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SikshaTheme.colors.Gray600,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = keywordCount.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SikshaTheme.colors.Orange500
        )
    }
}

@Composable
fun MenuKeywordStats(
    keywords: List<String>,
    keywordCounts: List<Long>,
    keywordIcons: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in keywords.ifEmpty { listOf("맛", "가격", "음식구성") }.indices) {
            MenuKeywordStat(
                keywordString = keywords.ifEmpty { listOf("맛", "가격", "음식구성") }[i],
                keywordCount = keywordCounts.ifEmpty { listOf<Long>(0, 0, 0) }[i],
                keywordIcon = keywordIcons[i]
            )
        }
    }
}

@Composable
@Preview
fun MenuKeywordStatsPreview() {
    MenuKeywordStats(
        keywords = listOf("맛", "가격", "음식구성"),
        keywordCounts = listOf(12, 3, 1),
        keywordIcons = listOf(
            { CancelIcon() },
            { CancelIcon() },
            { CancelIcon() }
        ),
        modifier = Modifier.height(150.dp)
    )
}
