package com.wafflestudio.siksha2.compose.ui.review

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R

@Composable
fun ReviewSpeechBubble(
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(R.drawable.ic_speech_bubble),
        contentDescription = null,
        modifier = modifier.offset(x = (-1).dp, y = 1.dp)
            .blur(10.dp),
        tint = Color(0xff000000),
    )
    Icon(
        painter = painterResource(R.drawable.ic_speech_bubble),
        contentDescription = null,
        modifier = modifier,
        tint = Color(0xffffffff)
    )
}

@Composable
@Preview
fun ReviewSpeechBubblePreview() {
    ReviewSpeechBubble(modifier = Modifier.fillMaxWidth())
}
