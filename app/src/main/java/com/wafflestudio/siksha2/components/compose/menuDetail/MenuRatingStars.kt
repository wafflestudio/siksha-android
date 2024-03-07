package com.wafflestudio.siksha2.components.compose.menuDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import kotlin.math.roundToInt

@Composable
private fun MenuReviewSingleStar(
    modifier: Modifier = Modifier,
    flag: Int
) {
    Image(
        painter = painterResource(
            when {
                flag <= 0 -> R.drawable.ic_full_star
                flag == 1 -> R.drawable.ic_half_star
                else -> R.drawable.ic_empty_star
            }
        ),
        contentDescription = null,
        modifier = modifier.heightIn(max = 48.dp).fillMaxHeight().aspectRatio(1f)
    )
}

@Composable
fun MenuRatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
    dragEnabled: Boolean = false,
    width: Int = 100,
    height: Int = 18
) {
    val rounds = (rating * 2).roundToInt()
    Row(
        modifier = modifier.width(width.dp).height(height.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..5) {
            MenuReviewSingleStar(
                flag = i * 2 - rounds
            )
        }
    }
}
