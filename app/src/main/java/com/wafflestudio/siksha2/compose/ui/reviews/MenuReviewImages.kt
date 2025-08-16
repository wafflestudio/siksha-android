package com.wafflestudio.siksha2.compose.ui.reviews

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun MenuReviewImage(
    imageUri: Uri,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            modifier = Modifier.fillMaxSize()
                .align(Alignment.Center),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MenuReviewImages(
    uris: List<Uri>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (uri in uris) {
            MenuReviewImage(
                imageUri = uri,
                modifier = Modifier.size(102.dp)
            )
        }
    }
}

@Composable
@Preview
fun MenuReviewImagePreview() {
    MenuReviewImage(
        imageUri = Uri.parse("https://picsum.photos/id/237"),
        modifier = Modifier.size(120.dp)
    )
}
