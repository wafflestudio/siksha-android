package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.utils.dpToSp

@Composable
fun ErrorComponent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_warning),
            contentDescription = null,
            colorFilter = ColorFilter.tint(SikshaColors.Gray800),
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = "네트워크 연결이 불안정합니다.",
            fontSize = dpToSp(20.dp),
            fontFamily = NanumSquareFontFamily,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}
