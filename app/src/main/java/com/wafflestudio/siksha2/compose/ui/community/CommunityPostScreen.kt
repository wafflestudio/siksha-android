package com.wafflestudio.siksha2.compose.ui.community

import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.siksha2.components.compose.EditText
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.ui.CommentIcon
import com.wafflestudio.siksha2.ui.EtcIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.ThumbIcon
import com.wafflestudio.siksha2.ui.main.community.CommunityPostViewModel
import com.wafflestudio.siksha2.ui.main.community.CommunityViewModel

@Composable
fun CommunityPostScreen(
    postId: Long,
    modifier: Modifier = Modifier,
    communityViewModel: CommunityViewModel = hiltViewModel(),
    postViewModel: CommunityPostViewModel = hiltViewModel()
) {
    val post by postViewModel.post.collectAsState()
    var commentInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        postViewModel.initialize(postId)
    }

    Column(
        modifier = modifier.background(SikshaColors.White900)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            TopBar(
                title = "hello"
            )
            Column(
                modifier = Modifier.padding(start = 35.dp, end = 35.dp, top = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = post.userId,
                        color = SikshaColors.Gray400,
                        fontSize = 12.sp,
                        style = SikshaTypography.body2
                    )
                    Text(
                        text = post.updatedAt,
                        color = SikshaColors.Gray400,
                        fontSize = 12.sp,
                        style = SikshaTypography.body2
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.title,
                    style = SikshaTypography.subtitle2,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.height(13.dp))
                Text(
                    text = post.content,
                    style = SikshaTypography.body2,
                )
//            Image()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ThumbIcon(isSelected = post.isLiked)
                            Text(
                                text = post.likeCount.toString(),
                                fontSize = 8.sp,
                                color = SikshaColors.OrangeMain,
                                style = SikshaTypography.body2
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CommentIcon()
                            Text(
                                text = post.commentCount.toString(),
                                fontSize = 8.sp,
                                color = SikshaColors.Gray600,
                                style = SikshaTypography.body2
                            )
                        }
                    }
                    EtcIcon()
                }
            }
            Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
            Column {

            }
        }
        Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 9.dp)
        ) {
            EditText(
                value = commentInput,
                onValueChange = {
                    commentInput = it
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = SikshaTypography.body2,
            )
        }
    }
}
