package com.wafflestudio.siksha2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class SikshaColors(
    Gray50: Color,
    Gray100: Color,
    Gray200: Color,
    Gray300: Color,
    Gray400: Color,
    Gray500: Color,
    Gray600: Color,
    Gray700: Color,
    Gray800: Color,
    Gray900: Color,
    Orange100: Color,
    Orange200: Color,
    Orange300: Color,
    Orange400: Color,
    Orange500: Color,
    Orange600: Color,
    Orange700: Color,
    Orange800: Color,
    Orange900: Color,
    OrangeTint: Color,
    White: Color,
    Black: Color,
    BackgroundMain: Color,
    AccentLike: Color,
    private val isDarkTheme: Boolean = false
) {
    var Gray50 by mutableStateOf(Gray50)
        private set
    var Gray100 by mutableStateOf(Gray100)
        private set
    var Gray200 by mutableStateOf(Gray200)
        private set
    var Gray300 by mutableStateOf(Gray300)
        private set
    var Gray400 by mutableStateOf(Gray400)
        private set
    var Gray500 by mutableStateOf(Gray500)
        private set
    var Gray600 by mutableStateOf(Gray600)
        private set
    var Gray700 by mutableStateOf(Gray700)
        private set
    var Gray800 by mutableStateOf(Gray800)
        private set
    var Gray900 by mutableStateOf(Gray900)
        private set
    var Orange100 by mutableStateOf(Orange100)
        private set
    var Orange200 by mutableStateOf(Orange200)
        private set
    var Orange300 by mutableStateOf(Orange300)
        private set
    var Orange400 by mutableStateOf(Orange400)
        private set
    var Orange500 by mutableStateOf(Orange500)
        private set
    var Orange600 by mutableStateOf(Orange600)
        private set
    var Orange700 by mutableStateOf(Orange700)
        private set
    var Orange800 by mutableStateOf(Orange800)
        private set
    var Orange900 by mutableStateOf(Orange900)
        private set
    var OrangeTint by mutableStateOf(OrangeTint)
        private set
    var White by mutableStateOf(White)
        private set
    var Black by mutableStateOf(Black)
        private set
    var BackgroundMain by mutableStateOf(BackgroundMain)
        private set
    var AccentLike by mutableStateOf(AccentLike)
        private set

    val BackgroundPrimary: Color
        get() = if (isDarkTheme) BackgroundMain else White
    val BackgroundSecondary: Color
        get() = if (isDarkTheme) Gray200 else White
    val BackgroundDark: Color
        get() = if (isDarkTheme) White else Black
    val BackgroundGNB: Color
        get() = if (isDarkTheme) BackgroundMain else Orange500
    val BackgroundToast: Color
        get() = if (isDarkTheme) Gray400 else Gray700
    val BorderPrimary: Color
        get() = if (isDarkTheme) Gray400 else Gray200
    val BorderSecondary: Color
        get() = if (isDarkTheme) Gray500 else Gray200
    val TextButton: Color
        get() = if (isDarkTheme) Black else White
    val TextAccent: Color
        get() = if (isDarkTheme) Black else Orange500
    val TextGNB: Color
        get() = if (isDarkTheme) Gray900 else White
    val TextDim: Color
        get() = if (isDarkTheme) Black else White
    val IconCloseBg: Color
        get() = if (isDarkTheme) Gray500 else Gray400
    val IconWhiteIcon: Color
        get() = if (isDarkTheme) Black else White
    val IconLike: Color
        get() = if (isDarkTheme) Gray500 else Gray200
    val ElementChip: Color
        get() = if (isDarkTheme) Gray50 else Gray100
    val ElementTooltip: Color
        get() = if (isDarkTheme) Gray400 else Gray100
    val ElementControl: Color
        get() = if (isDarkTheme) Gray500 else Gray200

    fun copy(
        Gray50: Color = this.Gray50,
        Gray100: Color = this.Gray100,
        Gray200: Color = this.Gray200,
        Gray300: Color = this.Gray300,
        Gray400: Color = this.Gray400,
        Gray500: Color = this.Gray500,
        Gray600: Color = this.Gray600,
        Gray700: Color = this.Gray700,
        Gray800: Color = this.Gray800,
        Gray900: Color = this.Gray900,
        Orange100: Color = this.Orange100,
        Orange200: Color = this.Orange200,
        Orange300: Color = this.Orange300,
        Orange400: Color = this.Orange400,
        Orange500: Color = this.Orange500,
        Orange600: Color = this.Orange600,
        Orange700: Color = this.Orange700,
        Orange800: Color = this.Orange800,
        Orange900: Color = this.Orange900,
        OrangeTint: Color = this.OrangeTint,
        White: Color = this.White,
        Black: Color = this.Black,
        BackgroundMain: Color = this.BackgroundMain,
        AccentLike: Color = this.AccentLike
    ) = SikshaColors(
        Gray50, Gray100, Gray200, Gray300, Gray400, Gray500, Gray600, Gray700, Gray800, Gray900, Orange100, Orange200, Orange300, Orange400, Orange500, Orange600, Orange700, Orange800, Orange900, OrangeTint, White, Black, BackgroundMain, AccentLike
    )

    fun updateColorsFrom(other: SikshaColors) {
        Gray50 = other.Gray50
        Gray100 = other.Gray100
        Gray200 = other.Gray200
        Gray300 = other.Gray300
        Gray400 = other.Gray400
        Gray500 = other.Gray500
        Gray600 = other.Gray600
        Gray700 = other.Gray700
        Gray800 = other.Gray800
        Gray900 = other.Gray900
        Orange100 = other.Orange100
        Orange200 = other.Orange200
        Orange300 = other.Orange300
        Orange400 = other.Orange400
        Orange500 = other.Orange500
        Orange600 = other.Orange600
        Orange700 = other.Orange700
        Orange800 = other.Orange800
        Orange900 = other.Orange900
        OrangeTint = other.OrangeTint
        White = other.White
        Black = other.Black
        BackgroundMain = other.BackgroundMain
        AccentLike = other.AccentLike
    }

    companion object {
        val Day = SikshaColors(
            Gray50 = Color(0xFFF8F8F8),
            Gray100 = Color(0xFFF2F3F4),
            Gray200 = Color(0xFFE5E6E9),
            Gray300 = Color(0xFFD8DADE),
            Gray400 = Color(0xFFCBCDD3),
            Gray500 = Color(0xFFBEC1C8),
            Gray600 = Color(0xFF989AA0),
            Gray700 = Color(0xFF727478),
            Gray800 = Color(0xFF4C4D50),
            Gray900 = Color(0xFF262728),
            Orange100 = Color(0xFFFFEAD3),
            Orange200 = Color(0xFFFFD5A7),
            Orange300 = Color(0xFFFFBF7A),
            Orange400 = Color(0xFFFFAA4E),
            Orange500 = Color(0xFFFF9522),
            Orange600 = Color(0xFFD27000),
            Orange700 = Color(0xFFA54C00),
            Orange800 = Color(0xFF7C2900),
            Orange900 = Color(0xFF570000),
            OrangeTint = Color(0x40FF9522),
            White = Color(0xFFFFFFFF),
            Black = Color(0xFF000000),
            BackgroundMain = Color(0xFFF8F8F8),
            AccentLike = Color(0xFFF86627)
        )
        val Night = SikshaColors(
            Gray50 = Color(0xFF1E1E1E),
            Gray100 = Color(0xFF202020),
            Gray200 = Color(0xFF232323),
            Gray300 = Color(0xFF282828),
            Gray400 = Color(0xFF2D2D2D),
            Gray500 = Color(0xFF404040),
            Gray600 = Color(0xFF919191),
            Gray700 = Color(0xFFB7B7B7),
            Gray800 = Color(0xFFCBCBCC),
            Gray900 = Color(0xFFE5E6E9),
            Orange100 = Color(0xFFF0DCC6),
            Orange200 = Color(0xFFF2CA9E),
            Orange300 = Color(0xFFF1B573),
            Orange400 = Color(0xFFF1A14A),
            Orange500 = Color(0xFFF28C1D),
            Orange600 = Color(0xFFC76A00),
            Orange700 = Color(0xFF984600),
            Orange800 = Color(0xFF6B2400),
            Orange900 = Color(0xFF410000),
            OrangeTint = Color(0x40F28C1D),
            White = Color(0xFF232323),
            Black = Color(0xFFFFFFFF),
            BackgroundMain = Color(0xFF121212),
            AccentLike = Color(0xFFF86627),
            isDarkTheme = true
        )
    }
}
