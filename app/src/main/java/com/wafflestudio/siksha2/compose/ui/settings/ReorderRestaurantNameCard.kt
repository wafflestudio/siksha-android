package com.wafflestudio.siksha2.compose.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun ReorderRestaurantNameCard(
    restaurantName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
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
