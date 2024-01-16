package com.wafflestudio.siksha2.compose.ui.review

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.utils.dpToSp


@Composable
fun ItemReviewImage(
    imageUri: Uri,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShowMore: () -> Unit = {},
    showMore: Int? = null,
    deletable: Boolean = false
) {
    Box(
        modifier = modifier
            .width(100.dp).height(100.dp)
            .clip(shape = RoundedCornerShape(8.dp))
    ) {
        Image(
            modifier = Modifier.fillMaxSize()
                .align(Alignment.Center)
                .clickable { if(showMore == null) onClick() },
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

@Composable
@Preview
fun ItemReviewImagePreview() {
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ItemReviewImage(
            imageUri = Uri.parse("https://postfiles.pstatic.net/MjAyMzExMjVfMjkw/MDAxNzAwOTIyODAzODEw.QqyDtG40dMPBGRLsCcplMuOgz-wTVx6aZ4UeeykP65Qg.Hscm0YN_F9pcPE2lndK0YV8eDKz7m2Hi1RMa0ocu2Wog.JPEG.jyurisenpai/20231125010057_1.jpg?type=w773")
        )
        ItemReviewImage(
            imageUri = Uri.parse("https://postfiles.pstatic.net/MjAyMzExMjZfMjcy/MDAxNzAwOTI3NTczMDY5.c9qVmjXE0nBVZpKukBxB9EB0LytpB5Olc6psLGQdWLQg.-D-zLjlG7bIPYm8XmYzK9-l1vitTZAGcimoHM57QATAg.JPEG.jyurisenpai/20231121203650_1.jpg?type=w773"),
            deletable = true
        )
        ItemReviewImage(
            imageUri = Uri.parse("https://postfiles.pstatic.net/MjAyMzExMjZfMjU5/MDAxNzAwOTI3MjgxMjAz.f7jMVmS7vYGWKWswaOnnxCjgmaN0qgSt0_2VLB-XZrog.WMa7r-nHh9zw_QXO15bA4ts2NabuiEtQQkM6XYSiA1Ug.JPEG.jyurisenpai/20231118200550_1.jpg?type=w773"),
            showMore = 3
        )
    }
}
