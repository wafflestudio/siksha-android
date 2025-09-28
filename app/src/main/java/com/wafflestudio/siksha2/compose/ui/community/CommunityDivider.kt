package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommunityDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        color = Color(0xFFF0F0F0),
        thickness = 1.dp,
        modifier = modifier.padding(horizontal = 7.5.dp)
    )
}
