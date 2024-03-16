package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.ThumbIcon

// TODO: 더 좋은 이름 찾기
@Composable
fun LikeIconWithCount(
    modifier: Modifier = Modifier,
    likeCount: Long = 0L,
    isLiked: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClick() }
    ) {
        ThumbIcon(isSelected = isLiked)
        Text(
            text = likeCount.toString(),
            fontSize = 8.sp,
            color = SikshaColors.OrangeMain,
            style = SikshaTypography.body2
        )
    }
}
