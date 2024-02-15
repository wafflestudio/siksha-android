package com.wafflestudio.siksha2.components.compose.menuDetail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.wafflestudio.siksha2.R

@Composable
fun ItemReviewImageDialog(
    url: Uri,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(0.dp, 330.dp)
                    .widthIn(0.dp, 300.dp)
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close_white),
                    contentDescription = "닫기",
                    modifier = Modifier.padding(bottom = 10.dp)
                        .size(20.dp)
                        .clickable {
                            onDismiss()
                        }
                        .align(Alignment.End)
                )
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .heightIn(0.dp, 300.dp)
                        .widthIn(0.dp, 300.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    )
}
