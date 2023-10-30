package com.wafflestudio.siksha2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

private val lightThemeColors = lightColors(
    primary = SikshaColors.OrangeMain,
    primaryVariant = SikshaColors.OrangeMain,
    onPrimary = SikshaColors.White900,
    error = SikshaColors.Red,
    background = SikshaColors.White900,
    onBackground = SikshaColors.Black900,
    surface = SikshaColors.White900,
    onSurface = SikshaColors.Black900,
)

@Composable
fun SikshaTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = lightThemeColors,
        typography = SikshaTypography,
        content = content
    )
}

@Preview
@Composable
fun Test() {
    SikshaTheme {
        Column {
            Text(
                modifier = Modifier.background(MaterialTheme.colors.primary),
                text = "h6",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary,
            )
            Surface {
                Column {
                    Text(
                        text = "body2",
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "subtitle1",
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        }
    }
}
