package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun CommunityDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        color = SikshaTheme.colors.Gray100,
        thickness = 1.dp,
        modifier = modifier.padding(horizontal = 7.5.dp)
    )
}
