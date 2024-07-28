package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography

@Composable
fun PostListItem(
    title: String,
    content: String,
    likeCount: Long,
    commentCount: Long,
    isLiked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    thumbnailImage: String? = null
) {
    Row(
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = SikshaTypography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = content,
                style = SikshaTypography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LikeIconWithCount(
                    likeCount = likeCount,
                    isLiked = isLiked
                )
                CommentIconWithCount(
                    commentCount = commentCount
                )
            }
        }
        thumbnailImage?.let {
            SubcomposeAsyncImage(
                model = it,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .size(61.dp),
                contentDescription = "",
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(10.dp)
                    )
                },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostListItemPreview() {
    SikshaTheme {
        PostListItem(
            title = "제목",
            content = "본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 본문 ",
            likeCount = 10,
            commentCount = 20,
            isLiked = false,
            thumbnailImage = "https://picsum.photos/200"
        )
    }
}
