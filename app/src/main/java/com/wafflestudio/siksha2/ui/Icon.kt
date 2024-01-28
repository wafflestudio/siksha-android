package com.wafflestudio.siksha2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R

@Composable
fun CommentIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SikshaColors.Gray600)
) {
    Image(
        modifier = modifier.size(12.dp),
        painter = painterResource(R.drawable.ic_comment),
        contentDescription = null,
        colorFilter = colorFilter
    )
}

@Composable
fun ThumbIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = ColorFilter.tint(SikshaColors.OrangeMain)
) {
    Image(
        modifier = modifier.size(12.dp),
        painter = painterResource(if (isSelected) R.drawable.ic_thumb_filled else R.drawable.ic_thumb_outline),
        contentDescription = null,
        colorFilter = colorFilter
    )
}

@Composable
fun EtcIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SikshaColors.Gray600)
) {
    Image(
        modifier = modifier.size(12.dp),
        painter = painterResource(R.drawable.ic_etc),
        contentDescription = null,
        colorFilter = colorFilter
    )
}

@Composable
fun NavigateUpIcon(
    modifier: Modifier = Modifier,
    color: Color = SikshaColors.White900
) {
    Image(
        modifier = modifier.size(16.dp),
        painter = painterResource(R.drawable.ic_back_arrow),
        contentDescription = "navigate up",
        colorFilter = ColorFilter.tint(color)
    )
}
