package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) SikshaColors.OrangeMain else SikshaColors.Gray100)
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        color = if (selected) SikshaColors.White900 else SikshaColors.Gray400,
        style = SikshaTypography.body1
    )
}

@Preview
@Composable
fun ChipPreview() {
    Row(
        modifier = Modifier
            .background(SikshaColors.White900)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Chip(
            text = "자유 게시판",
            selected = false
        )
        Chip(
            text = "리뷰 게시판",
            selected = true
        )
    }
}
