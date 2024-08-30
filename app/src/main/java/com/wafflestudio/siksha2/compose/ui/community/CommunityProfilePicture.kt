package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.wafflestudio.siksha2.R

@Composable
fun CommunityProfilePicture(
    model: String?,
    modifier: Modifier = Modifier
) {
    if (model == null) {
        Image(
            painter = painterResource(R.drawable.ic_rice_bowl_new),
            contentDescription = "",
            modifier = modifier.size(30.dp)
        )
    } else {
        SubcomposeAsyncImage(
            model = model,
            contentDescription = "",
            modifier = modifier
                .size(30.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
