package com.wafflestudio.siksha2.compose.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import kotlin.math.roundToInt

@Composable
private fun ItemReviewSingleStar(
    modifier: Modifier = Modifier,
    flag: Int
){
    Image(
        painter = painterResource(
            when {
                flag <= 0 -> R.drawable.ic_full_star
                flag == 1 -> R.drawable.ic_half_star
                else -> R.drawable.ic_empty_star
            }
        ),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun ItemRatingStars(
    rating: Float,
    dragEnabled: Boolean = false,
    size: Int = 0
) {
    val rounds = (rating * 2).roundToInt()
    val starMeasure = when(size) {
        2 -> 48.dp
        1 -> 33.dp
        else -> 18.dp
    }
    val gap = when(size) {
        2 -> 12.dp
        1 -> 8.dp
        else -> 4.dp
    }
    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        items(count = 5) { index ->
            ItemReviewSingleStar(
                flag = index*2 - rounds,
                modifier = Modifier.width(starMeasure)
                    .height(starMeasure)
            )
        }
    }
}

@Composable
@Preview
private fun StarsPreview(){
    Column() {
        ItemRatingStars(rating = 0.0f)
        ItemRatingStars(rating = 1.0f, size = 2)
        ItemRatingStars(rating = 2.5f, size = 1)
        ItemRatingStars(rating = 3.5f)
        ItemRatingStars(rating = 5.0f)
    }
}
