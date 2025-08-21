package com.wafflestudio.siksha2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
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
    onSurface = SikshaColors.Black900
)

@Composable
fun SikshaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = lightThemeColors,
        typography = SikshaTypography,
        content = content
    )
}

@Preview
@Composable
fun Example() {
    SikshaTheme {
        Column {
            Text(
                modifier = Modifier.background(MaterialTheme.colors.primary),
                text = "this is h6",
                style = SikshaTypography.h6,
                color = MaterialTheme.colors.onPrimary
            )
            Surface {
                Column {
                    Text(
                        text = "this is body2",
                        style = SikshaTypography.body2
                    )
                    Text(
                        text = "this is subtitle1",
                        style = SikshaTypography.subtitle1
                    )
                }
            }
            Button(
                onClick = {}
            ) {
                Text(
                    text = "this is button"
                )
            }
        }
    }
}
