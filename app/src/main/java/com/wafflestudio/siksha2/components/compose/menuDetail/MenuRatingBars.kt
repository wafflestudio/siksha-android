package com.wafflestudio.siksha2.components.compose.menuDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.utils.dpToSp
import kotlin.math.max

@Composable
fun MenuRatingBar(
    ratingIndex: Int,
    ratio: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ratingIndex.toString(),
            modifier = Modifier.padding(end = 3.dp),
            fontSize = dpToSp(8.dp),
            fontWeight = FontWeight.Bold,
            color = SikshaColors.Gray500
        )
        Image(
            painter = painterResource(R.drawable.ic_gray_star),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 9.dp)
                .width(8.dp)
                .height(8.dp)
        )
        if (ratio > 0.0f) {
            Box(
                modifier = Modifier
                    .weight(ratio)
                    .height(5.dp)
                    .background(
                        color = SikshaColors.OrangeMain,
                        shape = RoundedCornerShape(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(2.dp),
                            bottomEnd = CornerSize(2.dp),
                            bottomStart = CornerSize(0.dp)
                        )
                    )
            ) {}
        }
        if (ratio < 1.0f) {
            Spacer(modifier = Modifier.weight(1.0f - ratio))
        }
    }
}

@Composable
fun MenuRatingBars(
    distributions: List<Long>,
    modifier: Modifier = Modifier
) {
    val maxCount = max(distributions.max(), 1L)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        distributions.reversed().forEachIndexed { index, cnt ->
            MenuRatingBar(
                ratingIndex = 5 - index,
                ratio = cnt.toFloat() / maxCount.toFloat()
            )
        }
    }
}
