package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.wafflestudio.siksha2.components.compose.CommentIconWithCount
import com.wafflestudio.siksha2.components.compose.EditText
import com.wafflestudio.siksha2.components.compose.LikeIconWithCount
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Comment
import com.wafflestudio.siksha2.ui.EtcIcon
import com.wafflestudio.siksha2.ui.NavigateUpIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.PostDetailViewModel
import com.wafflestudio.siksha2.ui.main.community.PostListViewModel
import com.wafflestudio.siksha2.utils.toParsedTimeString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun PostDetailScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    postListViewModel: PostListViewModel = hiltViewModel(),
    postDetailViewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by postDetailViewModel.post.collectAsState()
    val board by postListViewModel.selectedBoard.collectAsState()
    val comments = postDetailViewModel.commentPagingData.collectAsLazyPagingItems()
    var commentInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.background(SikshaColors.White900)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            TopBar(
                title = board.name,
                navigationButton = {
                    NavigateUpIcon(
                        modifier = Modifier.clickable {
                            onNavigateUp()
                        }
                    )
                }
            )
            Column(
                modifier = Modifier.padding(start = 35.dp, end = 35.dp, top = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = post.nickname,
                        color = SikshaColors.Gray400,
                        fontSize = 12.sp,
                        style = SikshaTypography.body2
                    )
                    Text(
                        text = post.updatedAt.toParsedTimeString(),
                        color = SikshaColors.Gray400,
                        fontSize = 12.sp,
                        style = SikshaTypography.body2
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.title,
                    style = SikshaTypography.subtitle2,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(13.dp))
                Text(
                    text = post.content,
                    style = SikshaTypography.body2
                )
                Spacer(modifier = Modifier.height(16.dp))
                post.etc?.images?.let { images ->
                    HorizontalPager(
                        state = rememberPagerState(initialPage = 0, pageCount = { images.size }),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        images.forEach {
                            SubcomposeAsyncImage(
                                model = it,
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                loading = {
                                    CircularProgressIndicator()
                                },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LikeIconWithCount(
                            likeCount = post.likeCount,
                            isLiked = post.isLiked
                        )
                        CommentIconWithCount(
                            commentCount = post.commentCount
                        )
                    }
                    EtcIcon()
                }
            }
            Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
            LazyColumn {
                items(comments.itemCount) {
                    comments[it]?.let { it1 -> CommentItem(it1) }
                }
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
                leadingIcon = {
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    postDetailViewModel.addComment(commentInput)
                                    comments.refresh()
                                    commentInput = ""
                                }
                            }
                            .background(
                                color = SikshaColors.White900,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "올리기",
                            style = SikshaTypography.body2.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SikshaColors.OrangeMain
                            )
                        )
                    }
                },
                textStyle = SikshaTypography.body2
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SikshaColors.White900)
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 35.dp, vertical = 12.dp)
        ) {
            Row {
                Text(
                    text = comment.nickname,
                    style = SikshaTypography.body2
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = comment.updatedAt.toParsedTimeString(),
                    style = SikshaTypography.body2
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = comment.content,
                style = SikshaTypography.body2
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row {
                LikeIconWithCount(
                    likeCount = comment.likeCount,
                    isLiked = comment.isLiked
                )
                Spacer(modifier = Modifier.weight(1f))
                EtcIcon()
            }
        }
        Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
    }
}