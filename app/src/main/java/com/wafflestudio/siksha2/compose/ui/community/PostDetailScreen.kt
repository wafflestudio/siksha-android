package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.Checkbox
import com.wafflestudio.siksha2.components.compose.CommentIconWithCount
import com.wafflestudio.siksha2.components.compose.LikeIconWithCount
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Comment
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.ui.EtcIcon
import com.wafflestudio.siksha2.ui.NavigateUpIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.PostDetailViewModel
import com.wafflestudio.siksha2.ui.main.community.PostListViewModel
import com.wafflestudio.siksha2.utils.toParsedTimeString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PostDetailRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    postListViewModel: PostListViewModel = hiltViewModel(),
    postDetailViewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by postDetailViewModel.post.collectAsState()
    val board by postListViewModel.selectedBoard.collectAsState()
    val comments = postDetailViewModel.commentPagingData.collectAsLazyPagingItems()

    PostDetailScreen(
        post = post,
        board = board,
        comments = comments,
        onNavigateUp = onNavigateUp,
        togglePostLike = postDetailViewModel::togglePostLike,
        toggleCommentLike = postDetailViewModel::toggleCommentLike,
        updateListWithLikedPost = postListViewModel::updateListWithLikedPost,
        updateListWithCommentAddedPost = postListViewModel::updateListWithCommentAddedPost,
        addComment = postDetailViewModel::addComment,
        modifier = modifier
    )
}

@Composable
fun PostDetailScreen(
    post: Post,
    board: Board,
    comments: LazyPagingItems<Comment>,
    onNavigateUp: () -> Unit,
    togglePostLike: () -> Unit,
    toggleCommentLike: (Comment) -> Unit,
    updateListWithLikedPost: (Post) -> Unit,
    updateListWithCommentAddedPost: (Post) -> Unit,
    addComment: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentInput by remember { mutableStateOf("") }
    var isAnonymousInput by remember { mutableStateOf(true) }

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
                            togglePostLike()
                            updateListWithLikedPost(post)
                        }
                    )
                    Divider(thickness = 0.5.dp, color = SikshaColors.Gray400)
                }
                items(comments.itemCount) {
                    comments[it]?.let { comment ->
                        CommentItem(
                            comment = comment,
                            onClickLike = {
                                toggleCommentLike(comment)
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
            isAnonymous = isAnonymousInput,
            onIsAnonymousChanged = { isAnonymousInput = it },
            addComment = { ->
                addComment(commentInput, isAnonymousInput)
                updateListWithCommentAddedPost(post)
            },
            modifier = Modifier
                .padding(horizontal = 9.dp, vertical = 5.dp)
                .height(40.dp)
                .fillMaxWidth()
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
    isAnonymous: Boolean,
    onIsAnonymousChanged: (Boolean) -> Unit,
    addComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        CommentEditText(
            value = commentInput,
            onValueChange = onCommentInputChanged,
            modifier = Modifier.fillMaxWidth(),
            hint = stringResource(R.string.community_comment_hint),
            leadingIcon = {
                Row(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 12.dp)
                        .clickable(
                            onClick = {
                                onIsAnonymousChanged(isAnonymous.not())
                            },
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAnonymous,
                        onCheckedChange = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.community_comment_anonymous),
                        style = MaterialTheme.typography.body2.copy(
                            fontSize = 10.sp,
                            color = if (isAnonymous) MaterialTheme.colors.primary else SikshaColors.Gray400
                        )
                    )
                }
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .clickable {
                            addComment()
                            onCommentInputChanged("")
                        }
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.community_comment_send_button),
                        style = SikshaTypography.body2.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SikshaColors.White900
                        )
                    )
                }
            },
            textStyle = SikshaTypography.body2
        )
    }
}

@Preview(device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Composable
fun PostDetailScreenPreview() {
    SikshaTheme {
        PostDetailScreen(
            post = Post(),
            board = Board(),
            comments = flowOf(PagingData.empty<Comment>()).collectAsLazyPagingItems(),
            onNavigateUp = {},
            togglePostLike = {},
            updateListWithLikedPost = {},
            toggleCommentLike = {},
            addComment = { _, _ -> },
            updateListWithCommentAddedPost = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun CommentInputRowPreview() {
    SikshaTheme {
        CommentInputRow(
            commentInput = "test",
            onCommentInputChanged = {},
            isAnonymous = true,
            onIsAnonymousChanged = {},
            addComment = {}
        )
    }
}

@Preview
@Composable
fun CommentInputRowHintPreview() {
    SikshaTheme {
        CommentInputRow(
            commentInput = "",
            onCommentInputChanged = {},
            isAnonymous = false,
            onIsAnonymousChanged = {},
            addComment = {}
        )
    }
}
