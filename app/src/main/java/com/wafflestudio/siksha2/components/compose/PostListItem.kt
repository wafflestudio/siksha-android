package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.CommentIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.ThumbIcon

@Composable
fun PostListItem(
    title: String,
    content: String,
    likeCount: Long,
    commentCount: Long,
    isLiked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = modifier
                .clickable {
                    onClick()
                }
                .padding(start = 35.dp, end = 21.dp, top = 15.dp, bottom = 15.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = SikshaTypography.body2
                )
                Text(
                    text = content,
                    style = SikshaTypography.body2
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ThumbIcon(isSelected = isLiked)
                        Text(
                            text = likeCount.toString(),
                            fontSize = 8.sp,
                            color = SikshaColors.OrangeMain,
                            style = SikshaTypography.body2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CommentIcon()
                        Text(
                            text = commentCount.toString(),
                            fontSize = 8.sp,
                            color = SikshaColors.Gray600,
                            style = SikshaTypography.body2
                        )
                    }
                }
            }
        }
        Divider(color = SikshaColors.Gray400, thickness = 0.5.dp)
    }
}
