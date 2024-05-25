package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
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
import com.wafflestudio.siksha2.models.Post
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
            LazyColumn {
                item {
                    PostBody(
                        post = post,
                        onClickLike = {
                            scope.launch {
                                when (post.isLiked) {
                                    true -> postDetailViewModel.unlikePost()
                                    false -> postDetailViewModel.likePost()
                                }
                                postListViewModel.updateListWithLikedPost(post)
                            }
                        }
                    )
                    Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
                }
                items(comments.itemCount) {
                    comments[it]?.let { comment ->
                        CommentItem(
                            comment = comment,
                            onClickLike = {
                                scope.launch {
                                    when (comment.isLiked) {
                                        true -> postDetailViewModel.unlikeComment(comment.id)
                                        false -> postDetailViewModel.likeComment(comment.id)
                                    }
                                    comments.refresh()
                                }
                            }
                        )
                    }
                }
            }
        }
        Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
        CommentInputRow(
            commentInput = commentInput,
            onCommentInputChanged = { commentInput = it },
            addComment = postDetailViewModel::addComment
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostBody(
    post: Post,
    modifier: Modifier = Modifier,
    onClickLike: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(top = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 35.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.nickname ?: "",
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
            modifier = Modifier.padding(horizontal = 35.dp),
            style = SikshaTypography.subtitle2,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(13.dp))
        Text(
            text = post.content,
            modifier = Modifier.padding(horizontal = 35.dp),
            style = SikshaTypography.body2
        )
        Spacer(modifier = Modifier.height(16.dp))
        post.etc?.images?.let { images ->
            HorizontalPager(
                state = rememberPagerState(pageCount = { images.size }),
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 35.dp),
                pageSize = PageSize.Fill
            ) {
                val startPadding = if (it == 0) 0.dp else 8.dp
                val endPadding = if (it == images.size - 1) 0.dp else 8.dp
                SubcomposeAsyncImage(
                    model = images[it],
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(start = startPadding, end = endPadding),
                    contentDescription = "",
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LikeIconWithCount(
                    likeCount = post.likeCount,
                    isLiked = post.isLiked,
                    onClick = onClickLike
                )
                CommentIconWithCount(
                    commentCount = post.commentCount
                )
            }
            EtcIcon()
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    onClickLike: () -> Unit = {}
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
                    color = SikshaColors.Gray400,
                    fontSize = 12.sp,
                    style = SikshaTypography.body2
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = comment.updatedAt.toParsedTimeString(),
                    color = SikshaColors.Gray400,
                    fontSize = 12.sp,
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
                    isLiked = comment.isLiked,
                    onClick = onClickLike
                )
                Spacer(modifier = Modifier.weight(1f))
                EtcIcon()
            }
        }
        Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
    }
}

@Composable
fun CommentInputRow(
    commentInput: String,
    onCommentInputChanged: (String) -> Unit,
    addComment: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        EditText(
            value = commentInput,
            onValueChange = onCommentInputChanged,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {},
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .clickable {
                            addComment(commentInput)
                            onCommentInputChanged("")
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
