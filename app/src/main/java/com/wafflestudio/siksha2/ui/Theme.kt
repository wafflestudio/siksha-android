package com.wafflestudio.siksha2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

private val lightThemeColors = lightColors(
    primary = SikshaColors.Day.Orange500,
    primaryVariant = SikshaColors.Day.Orange500,
    onPrimary = SikshaColors.Day.White,
    error = SikshaColors.Day.AccentLike,
    background = SikshaColors.Day.White,
    onBackground = SikshaColors.Day.Black,
    surface = SikshaColors.Day.White,
    onSurface = SikshaColors.Day.Black
)

private val darkThemeColors = darkColors(
    primary = SikshaColors.Night.Orange500,
    primaryVariant = SikshaColors.Night.Orange500,
    onPrimary = SikshaColors.Night.White,
    error = SikshaColors.Night.AccentLike,
    background = SikshaColors.Night.White,
    onBackground = SikshaColors.Night.Black,
    surface = SikshaColors.Night.White,
    onSurface = SikshaColors.Night.Black
)

// @Composable
// fun SikshaTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    content: @Composable () -> Unit
// ) {
//    val colors = if (darkTheme) darkThemeColors else lightThemeColors
//    MaterialTheme(
//        colors = colors,
//        typography = SikshaTypography,
//        content = content
//    )
// }

@Composable
fun SikshaTheme(
    lightColors: SikshaColors = SikshaColors.Day,
    darkColors: SikshaColors = SikshaColors.Night,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    typography: Typography = SikshaTypography,
    content: @Composable () -> Unit
) {
    val currentColor = remember { if (isDarkTheme) darkColors else lightColors }
    val rememberedColors = remember { currentColor.copy() }.apply { updateColorsFrom(currentColor) }
    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalTypography provides typography
    ) {
        ProvideTextStyle(SikshaTypography.h5, content = content)
    }
}

val LocalColors = staticCompositionLocalOf { SikshaColors.Day }
val LocalTypography = staticCompositionLocalOf { SikshaTypography }

object SikshaTheme {
    val colors: SikshaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
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
