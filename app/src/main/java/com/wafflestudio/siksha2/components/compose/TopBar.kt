package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.NavigateUpIcon
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationButton: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(SikshaTheme.colors.BackgroundGNB)
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            color = SikshaTheme.colors.TextGNB,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            style = SikshaTypography.subtitle1
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            navigationButton()
        }
    }
}

@Preview
@Composable
fun TopBarPreview() {
    SikshaTheme {
        TopBar(
            title = "테스트",
            navigationButton = { NavigateUpIcon() }
        )
    }
}
