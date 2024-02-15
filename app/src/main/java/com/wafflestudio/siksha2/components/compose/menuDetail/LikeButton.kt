package com.wafflestudio.siksha2.components.compose.menuDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.wafflestudio.siksha2.R

@Composable
fun LikeButton(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Image(
        painter = painterResource(
            if (isChecked) {
                R.drawable.ic_full_heart_small
            } else {
                R.drawable.ic_empty_heart_small
            }
        ),
        contentDescription = "좋아요",
        modifier = modifier.clickable { onClick() }
    )
}
