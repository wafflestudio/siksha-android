package com.wafflestudio.siksha2.components.festival

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.main.restaurant.DailyRestaurantViewModel

@Composable
fun FestivalToggleRoute(
    onClick: () -> Unit,
    vm: DailyRestaurantViewModel,
    modifier: Modifier = Modifier
) {
    val checked by vm.showFestival.collectAsState()
    FestivalToggle(
        modifier = modifier,
        checked = checked,
        onClick = onClick
    )
}

@Composable
fun FestivalToggle(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .padding(end = 18.dp)
            .width(50.dp)
            .height(24.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Crossfade(
            targetState = checked,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            ),
            label = "축제 메뉴 토글 스위치"
        ) { isChecked ->
            if (isChecked) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.festival_toggle_active),
                    contentDescription = "축제 메뉴 보기"
                )
            } else {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.festival_toggle_inactive),
                    contentDescription = "일반 메뉴 보기"
                )
            }
        }
    }
}
