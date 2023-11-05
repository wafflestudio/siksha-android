package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography

@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) SikshaColors.OrangeMain else SikshaColors.Gray100)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        color = if (selected) SikshaColors.White900 else SikshaColors.Gray400,
        style = SikshaTypography.body1,
    )
}

@Preview
@Composable
fun ChipPreview() {
    Row(
        modifier = Modifier.background(SikshaColors.White900)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Chip(
            text = "자유 게시판",
            selected = false,
        )
        Chip(
            text = "리뷰 게시판",
            selected = true,
        )
    }
}
