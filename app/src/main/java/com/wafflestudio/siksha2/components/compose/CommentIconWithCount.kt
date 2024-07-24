package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.CommentIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography

// TODO: 더 좋은 이름 찾기
@Composable
fun CommentIconWithCount(
    modifier: Modifier = Modifier,
    commentCount: Long = 0L
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        CommentIcon()
        Text(
            text = commentCount.toString(),
            fontSize = 10.sp,
            color = SikshaColors.Gray600,
            style = SikshaTypography.body2
        )
    }
}
