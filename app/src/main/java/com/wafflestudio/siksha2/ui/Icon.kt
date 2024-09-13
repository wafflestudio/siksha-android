package com.wafflestudio.siksha2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    Box(
        modifier = modifier.size(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back_arrow),
            contentDescription = "navigate up",
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun ExpandOptionsIcon(
    modifier: Modifier = Modifier,
    color: Color = SikshaColors.White900
) {
    Image(
        modifier = modifier
            .size(10.dp)
            .rotate(270f),
        painter = painterResource(R.drawable.ic_back_arrow),
        contentDescription = "expand options",
        colorFilter = ColorFilter.tint(color)
    )
}

@Composable
fun CancelIcon(
    modifier: Modifier = Modifier,
    color: Color = SikshaColors.White900
) {
    Box(
        modifier = modifier.size(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = modifier.size(12.dp),
            painter = painterResource(R.drawable.ic_cancel),
            contentDescription = "cancel",
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun DeletePostImageIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(22.dp),
        painter = painterResource(R.drawable.ic_delete_post_image),
        contentDescription = "delete image"
    )
}

@Composable
fun AddPostImageIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(106.dp),
        painter = painterResource(R.drawable.ic_add_post_image),
        contentDescription = "add image"
    )
}

@Composable
fun NewPostIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(50.dp),
        painter = painterResource(R.drawable.ic_new_post),
        contentDescription = "글쓰기"
    )
}

@Composable
fun CheckSimpleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SikshaColors.OrangeMain)
) {
    Image(
        modifier = modifier.size(9.dp),
        painter = painterResource(R.drawable.ic_check_simple),
        colorFilter = colorFilter,
        contentDescription = "선택됨"
    )
}

@Composable
fun SpeechBubbleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SikshaColors.Gray600)
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_speech_bubble),
        colorFilter = colorFilter,
        contentDescription = ""
    )
}
