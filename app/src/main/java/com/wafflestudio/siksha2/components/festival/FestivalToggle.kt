package com.wafflestudio.siksha2.components.festival

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaColors
import kotlin.math.roundToInt

@Composable
fun FestivalToggle(
    checked: State<Boolean>,
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
        val density = LocalDensity.current
        // 배경색
        Crossfade(
            targetState = checked.value,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            ),
            label = "축제 메뉴 토글 스위치"
        ) { isChecked ->
            if (isChecked) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.festival_toggle_background_active),
                    contentDescription = "축제 메뉴 보기"
                )
            } else {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.festival_toggle_background_inactive),
                    contentDescription = "일반 메뉴 보기"
                )
            }
        }

        // text
        val textOffsetActive = with(density) { 6.dp.toPx() }
        val textOffsetInactive = with(density) { 26.dp.toPx() }
        val textState by animateFloatAsState(
            targetValue = if (checked.value) textOffsetActive else textOffsetInactive,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            ),
            label = ""
        )
        Text(
            modifier = Modifier.align(Alignment.CenterStart)
                .offset { IntOffset(textState.roundToInt(), 0) },
            text = "축제",
            color = SikshaColors.White900,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        // knob
        val knobOffsetActive = with(density) { 28.dp.toPx() }
        val knobOffsetInactive = with(density) { 2.dp.toPx() }
        val knobState by animateFloatAsState(
            targetValue = if (checked.value) knobOffsetActive else knobOffsetInactive,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            ),
            label = ""
        )
        Image(
            modifier = Modifier.width(20.dp).height(20.dp).align(Alignment.CenterStart)
                .offset { IntOffset(knobState.roundToInt(), 0) },
            painter = painterResource(R.drawable.festival_toggle_knob),
            contentDescription = null
        )
    }
}
