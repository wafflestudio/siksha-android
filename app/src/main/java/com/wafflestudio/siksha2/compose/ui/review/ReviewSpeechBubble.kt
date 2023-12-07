package com.wafflestudio.siksha2.compose.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R

@Composable
fun ReviewSpeechBubble(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.ic_speech_bubble),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-1).dp, y = 1.dp)
                .blur(4.dp),
            colorFilter = ColorFilter.tint(Color(0x26000000))
        )
        Image(
            painter = painterResource(R.drawable.ic_speech_bubble),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color(0xffffffff))
        )
    }
}

@Composable
@Preview
fun ReviewSpeechBubblePreview() {
    Column {
        ReviewSpeechBubble(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = Color(0xff00ff00))
        )
        ReviewSpeechBubble(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color(0xff0000ff))
        )
    }
}
