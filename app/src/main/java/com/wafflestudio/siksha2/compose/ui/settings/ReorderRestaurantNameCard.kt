package com.wafflestudio.siksha2.compose.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun ReorderRestaurantNameCard(
    restaurantName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Transparent
            )
            .background(
                color = SikshaTheme.colors.ElementTooltip2
            )
            .padding(start = 12.dp, top = 11.dp, bottom = 11.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = restaurantName,
            fontSize = 13.sp,
            color = SikshaTheme.colors.Gray800,
            maxLines = 1
        )
    }
}
