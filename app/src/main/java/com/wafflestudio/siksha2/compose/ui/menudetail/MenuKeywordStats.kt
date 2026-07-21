package com.wafflestudio.siksha2.compose.ui.menudetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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

private val DefaultKeywords = listOf("맛", "가격", "음식구성")

@Composable
fun MenuKeywordStat(
    keywordString: String,
    keywordCount: Long,
    keywordTotal: Long,
    keywordIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .background(color = SikshaTheme.colors.Gray100, shape = RoundedCornerShape(8.dp))
    ) {
        if (keywordCount > 0 && keywordTotal > 0) {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier.fillMaxHeight()
                        .fillMaxWidth((keywordCount.toFloat() / keywordTotal.toFloat()).coerceIn(0f, 1f))
                        .align(Alignment.CenterStart)
                        .background(color = SikshaTheme.colors.OrangeTint, shape = RoundedCornerShape(8.dp))
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 6.dp, bottom = 6.dp, start = 14.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            keywordIcon()
            Spacer(Modifier.width(6.dp))
            Text(
                text = keywordString,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (keywordString in DefaultKeywords) SikshaTheme.colors.Gray600 else SikshaTheme.colors.Gray800,
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
}

@Composable
fun MenuKeywordStats(
    keywords: List<String>,
    keywordCounts: List<Long>,
    keywordTotals: List<Long>,
    keywordIcons: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        DefaultKeywords.forEachIndexed { i, defaultKeyword ->
            val keyword = keywords.getOrNull(i)
                ?.takeIf { it.isNotBlank() }
                ?: defaultKeyword

            MenuKeywordStat(
                keywordString = keyword,
                keywordCount = keywordCounts.getOrElse(i) { 0L },
                keywordTotal = keywordTotals.getOrElse(i) { 0L },
                keywordIcon = keywordIcons.getOrElse(i) { { CancelIcon() } }
            )
        }
    }
}

@Composable
@Preview
fun MenuKeywordStatsPreview() {
    MenuKeywordStats(
        keywords = listOf("맛", "가격", "음식구성"),
        keywordCounts = listOf(6, 2, 1),
        keywordTotals = listOf(12, 3, 1),
        keywordIcons = listOf(
            { CancelIcon() },
            { CancelIcon() },
            { CancelIcon() }
        ),
        modifier = Modifier.height(300.dp)
    )
}
