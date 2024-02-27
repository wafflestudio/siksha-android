package com.wafflestudio.siksha2.components.compose.menuDetail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.utils.dpToSp

@Composable
fun MenuReviewImage(
    imageUri: Uri,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
    onShowMore: () -> Unit = {},
    showMore: Int? = null,
    deletable: Boolean = false
) {
    var imageDialogState by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        if (imageDialogState && showMore == null) {
            MenuReviewImageDialog(url = imageUri, onDismiss = { imageDialogState = false })
        }
        Image(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .clickable {
                    if (showMore == null) imageDialogState = true
                },
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        if (deletable) {
            Image(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onDelete() },
                painter = painterResource(id = R.drawable.ic_image_delete),
                contentDescription = null
            )
        }
        if (showMore != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0x80000000))
                    .zIndex(1f)
                    .align(Alignment.Center)
                    .clickable { onShowMore() }
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "+",
                        fontSize = dpToSp(12.dp),
                        color = SikshaColors.White900
                    )
                    Text(
                        text = showMore.toString() + "건 더 보기",
                        fontSize = dpToSp(12.dp),
                        color = SikshaColors.White900
                    )
                }
            }
        }
    }
}
