package com.wafflestudio.siksha2.compose.ui.menudetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
private fun MenuReviewSingleStar(
    flag: Int,
    modifier: Modifier = Modifier
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
    initialRating: Float,
    modifier: Modifier = Modifier,
    changeEnabled: Boolean = false,
    width: Dp = 100.dp,
    height: Dp = 18.dp
) {
    val bounds = remember { mutableMapOf<Int, Rect>() }
    var rating by remember { mutableFloatStateOf(initialRating) }

    Row(
        modifier = modifier.width(width).height(height)
            .then(
                if (changeEnabled) {
                    Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            if (abs(dragAmount) < 25f) return@detectHorizontalDragGestures
                            val (index, _) = bounds.entries.find { (_, rect) ->
                                rect.contains(Offset(change.position.x, 0f))
                            } ?: return@detectHorizontalDragGestures
                            rating = index.toFloat()
                        }
                    }
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..5) {
            MenuReviewSingleStar(
                flag = i * 2 - (rating * 2).roundToInt(),
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    bounds[i] = layoutCoordinates.boundsInParent()
                }.then(
                    if (changeEnabled) {
                        Modifier.pointerInput(Unit) {
                            rating = i.toFloat()
                        }
                    } else {
                        Modifier
                    }
                )
            )
        }
    }
}

@Preview
@Composable
fun MenuRatingStarsPreview() {
}
