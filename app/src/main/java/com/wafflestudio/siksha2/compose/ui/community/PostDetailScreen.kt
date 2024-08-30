package com.wafflestudio.siksha2.compose.ui.community

import PostDetailDialog
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.wafflestudio.siksha2.ui.ThumbIcon
import com.wafflestudio.siksha2.ui.main.community.PostDetailEvent
import com.wafflestudio.siksha2.ui.main.community.PostDetailViewModel
import com.wafflestudio.siksha2.ui.main.community.PostListViewModel
import com.wafflestudio.siksha2.ui.main.community.UserPostListViewModel
import com.wafflestudio.siksha2.utils.toParsedTimeString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PostDetailRoute(
    onNavigateUp: () -> Unit,
    onNavigateToPostReport: (Long) -> Unit,
    onNavigateToCommentReport: (Long) -> Unit,
    onNavigateToPostEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    postListViewModel: PostListViewModel = hiltViewModel(),
    userPostListViewModel: UserPostListViewModel = hiltViewModel(),
    postDetailViewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by postDetailViewModel.post.collectAsState()
    val board by postListViewModel.selectedBoard.collectAsState()
    val comments = postDetailViewModel.commentPagingData.collectAsLazyPagingItems()
    val isAnonymous by postDetailViewModel.isAnonymous.collectAsState()

    PostDetailScreen(
        post = post,
        board = board,
        comments = comments,
        postDetailEvent = postDetailViewModel.postDetailEvent,
        isAnonymous = isAnonymous,
        onNavigateUp = onNavigateUp,
        onNavigateToPostReport = onNavigateToPostReport,
        onNavigateToCommentReport = onNavigateToCommentReport,
        onNavigateToPostEdit = onNavigateToPostEdit,
        refreshComments = { comments.refresh() },
        togglePostLike = postDetailViewModel::togglePostLike,
        toggleCommentLike = postDetailViewModel::toggleCommentLike,
        updateListWithLikedPost = postListViewModel::updateListWithLikedPost,
        updateListWithCommentAddedPost = postListViewModel::updateListWithCommentAddedPost,
        updateUserListWithLikedPost = userPostListViewModel::updateUserListWithLikedPost,
        updateUserListWithCommentAddedPost = userPostListViewModel::updateUserListWithCommentAddedPost,
        addComment = postDetailViewModel::addComment,
        deletePost = postDetailViewModel::deletePost,
        onIsAnonymousChanged = postDetailViewModel::setIsAnonymous,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostDetailScreen(
    post: Post,
    board: Board,
    comments: LazyPagingItems<Comment>,
    postDetailEvent: SharedFlow<PostDetailEvent>,
    isAnonymous: Boolean,
    onNavigateUp: () -> Unit,
    onNavigateToPostReport: (Long) -> Unit,
    onNavigateToCommentReport: (Long) -> Unit,
    onNavigateToPostEdit: (Long) -> Unit,
    togglePostLike: () -> Unit,
    refreshComments: () -> Unit,
    toggleCommentLike: (Comment) -> Unit,
    updateListWithLikedPost: (Post) -> Unit,
    updateListWithCommentAddedPost: (Post) -> Unit,
    updateUserListWithLikedPost: (Post) -> Unit,
    updateUserListWithCommentAddedPost: (Post) -> Unit,
    addComment: (String, Boolean) -> Unit,
    deletePost: (Long) -> Unit,
    onIsAnonymousChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentInput by remember { mutableStateOf("") }
    val commentListState = rememberLazyListState()
    var isPostDialogShowed by remember { mutableStateOf(false) }
    var isConfirmDeleteDialogShowed by remember { mutableStateOf(false) }

    if (isPostDialogShowed) {
        PostDetailDialog(
            onDismissRequest = {
                isPostDialogShowed = false
            },
            onClickEdit = {
                onNavigateToPostEdit(post.id)
            },
            onClickDelete = {
                isConfirmDeleteDialogShowed = true
                isPostDialogShowed = false
            },
            onClickReport = {
                isPostDialogShowed = false
                onNavigateToPostReport(post.id)
            },
            onClickCancel = {
                isPostDialogShowed = false
            }
        )
    }

    if (isConfirmDeleteDialogShowed) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.community_post_delete_dialog_title),
            description = stringResource(R.string.community_post_delete_dialog_description),
            onDismissRequest = {
                isConfirmDeleteDialogShowed = false
            },
            onConfirmDelete = {
                deletePost(post.id)
            },
            onCancelDelete = {
                isConfirmDeleteDialogShowed = false
            }
        )
    }

    PostDetailViewEventEffect(
        postDetailEvent = postDetailEvent,
        refreshComments = refreshComments,
        onNavigateUp = onNavigateUp
    )

    AutoScrollCommentsEffect(
        commentPagingItems = comments,
        commentsListState = commentListState
    )

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
            LazyColumn(state = commentListState) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    PostHeader(
                        post = post,
                        onClick = { isPostDialogShowed = true }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    PostBody(
                        post = post,
                        onClickLike = {
                            togglePostLike()
                            updateListWithLikedPost(post)
                            updateUserListWithLikedPost(post)
                        }
                    )
                    CommunityDivider()
                }
                items(comments.itemCount) {
                    comments[it]?.let { comment ->
                        CommentItem(
                            comment = comment,
                            modifier = Modifier.fillMaxWidth(),
                            onClickLike = {
                                toggleCommentLike(comment)
                            },
                            onNavigateToCommentReport = onNavigateToCommentReport,
                            onClickReport = {
                                onNavigateToCommentReport(comment.id)
                            }
                        )
                        CommunityDivider()
                    }
                }
            }
        }
        CommunityDivider()
        CommentInputRow(
            commentInput = commentInput,
            onCommentInputChanged = { commentInput = it },
            isAnonymous = isAnonymous,
            onIsAnonymousChanged = onIsAnonymousChanged,
            addComment = { ->
                addComment(commentInput, isAnonymous)
                updateListWithCommentAddedPost(post)
                updateUserListWithCommentAddedPost(post)
            },
            modifier = Modifier
                .padding(horizontal = 9.dp, vertical = 5.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun PostDetailViewEventEffect(
    postDetailEvent: SharedFlow<PostDetailEvent>,
    refreshComments: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        postDetailEvent.collect {
            when (it) {
                is PostDetailEvent.AddCommentSuccess -> {
                    refreshComments()
                }

                is PostDetailEvent.AddCommentFailed -> {
                    Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }

                is PostDetailEvent.ToggleCommentLikeSuccess -> {
                    refreshComments()
                }

                is PostDetailEvent.ToggleCommentLikeFailed -> {
                    Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.DeletePostSuccess -> {
                    onNavigateUp()
                    Toast.makeText(context, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.DeletePostFailed -> {
                    Toast.makeText(context, "게시물 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.DeleteCommentSuccess -> {
                    refreshComments()
                    Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.DeleteCommentFailed -> {
                    Toast.makeText(context, "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

/*
 * 댓글이 추가되면 자동으로 스크롤해주는 Effect
 */
@Composable
private fun AutoScrollCommentsEffect(
    commentPagingItems: LazyPagingItems<Comment>,
    commentsListState: LazyListState
) {
    var previousComments by remember { mutableStateOf(commentPagingItems.itemSnapshotList.map { it?.id }) }
    val newComments = commentPagingItems.itemSnapshotList.map { it?.id }
    LaunchedEffect(newComments) {
        if (newComments == previousComments) return@LaunchedEffect

        if (newComments.dropLast(1) == previousComments) { // 맨 끝에 하나만 추가된 상황이라면
            commentsListState.animateScrollToLastItem() // 마지막 댓글으로 스크롤
        }
        previousComments = newComments
    }
}

private suspend fun LazyListState.animateScrollToLastItem() {
    val targetIndex = layoutInfo.totalItemsCount - 1
    if (targetIndex < 0) return
    animateScrollToItem(targetIndex)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostHeader(
    post: Post,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommunityProfilePicture(model = post.profilePicture)
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(top = 2.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.nickname,
                color = SikshaColors.Black900,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                style = SikshaTypography.body2
            )
            Text(
                text = post.updatedAt.toParsedTimeString(),
                color = SikshaColors.Gray400,
                fontSize = 10.sp,
                style = SikshaTypography.body2
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        EtcIcon(
            modifier = Modifier
                .size(16.dp)
                .clickable { onClick() }
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
        modifier = modifier
    ) {
        Text(
            text = post.title,
            modifier = Modifier.padding(horizontal = 20.dp),
            style = SikshaTypography.subtitle2,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(13.dp))
        Text(
            text = post.content,
            modifier = Modifier.padding(horizontal = 20.dp),
            style = SikshaTypography.body2
        )
        Spacer(modifier = Modifier.height(20.dp))
        post.etc?.images?.let { images ->
            HorizontalPager(
                state = rememberPagerState(pageCount = { images.size }),
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
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
                        CircularProgressIndicator(
                            modifier = Modifier.padding(100.dp)
                        )
                    },
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = modifier.height(12.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            LikeIconWithCount(
                likeCount = post.likeCount,
                isLiked = post.isLiked
            )
            Spacer(modifier = Modifier.width(10.dp))
            CommentIconWithCount(
                commentCount = post.commentCount
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        PostLikeButton(
            modifier = Modifier.padding(start = 20.dp),
            isLiked = post.isLiked,
            onClick = onClickLike
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PostLikeButton(
    onClick: () -> Unit,
    isLiked: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = SikshaColors.OrangeMain,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(
                color = if (isLiked) SikshaColors.OrangeMain else SikshaColors.White900
            )
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThumbIcon(
            modifier = Modifier.size(11.dp),
            colorFilter = ColorFilter.tint(if (isLiked) SikshaColors.White900 else SikshaColors.OrangeMain)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = stringResource(R.string.community_post_like),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (isLiked) SikshaColors.White900 else SikshaColors.OrangeMain
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    onClickLike: () -> Unit = {},
    onNavigateToCommentReport: (Long) -> Unit,
    onClickReport: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommunityProfilePicture(
                    model = comment.profilePicture,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = comment.nickname,
                    color = SikshaColors.Black900,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    style = SikshaTypography.body2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.updatedAt.toParsedTimeString(),
                    color = SikshaColors.Gray400,
                    fontSize = 10.sp,
                    style = SikshaTypography.body2
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = comment.content,
                style = SikshaTypography.body2
            )
            Spacer(modifier = Modifier.height(10.dp))
            EtcIcon(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
                    .clickable {
                        showDialog = true
                    }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        CommentLikeButton(
            likeCount = comment.likeCount,
            isLiked = comment.isLiked,
            onClick = onClickLike,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

    if (showDialog) {
        CommentDetailDialog(
            onDismissRequest = { showDialog = false },
            onClickDelete = {},
            onClickReport = {
                showDialog = false
                onNavigateToCommentReport(comment.id)
            },
            onClickCancel = {
                showDialog = false
            }
        )
    }
}

@Composable
fun CommentLikeButton(
    likeCount: Long,
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color = SikshaColors.Gray100)
            .clickable { onClick() }
            .size(width = 35.dp, height = 53.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ThumbIcon(
            isSelected = isLiked,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = likeCount.toString(),
            fontSize = 10.sp,
            color = SikshaColors.OrangeMain,
            style = SikshaTypography.body2
        )
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
    CommentEditText(
        value = commentInput,
        onValueChange = onCommentInputChanged,
        modifier = modifier
            .heightIn(max = 100.dp)
            .fillMaxWidth(),
        hint = stringResource(R.string.community_comment_hint),
        leadingIcon = {
            Row(
                modifier = Modifier
                    .padding(start = 11.dp, end = 12.dp)
                    .height(40.dp)
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isAnonymous) MaterialTheme.colors.primary else SikshaColors.Gray400
                )
            }
        },
        trailingIcon = {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .padding(start = 12.dp, end = 6.dp),
                contentAlignment = Alignment.Center
            ) {
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
                        .padding(horizontal = 11.dp, vertical = 6.dp)
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
            }
        }
    )
}

/*@Preview(device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Composable
fun PostDetailScreenPreview() {
    SikshaTheme {
        PostDetailScreen(
            post = Post(),
            board = Board(),
            comments = flowOf(PagingData.empty<Comment>()).collectAsLazyPagingItems(),
            postDetailEvent = MutableSharedFlow(),
            isAnonymous = true,
            onNavigateUp = {},
            togglePostLike = {},
            refreshComments = {},
            updateListWithLikedPost = {},
            toggleCommentLike = {},
            addComment = { _, _ -> },
            updateListWithCommentAddedPost = {},
            updateUserListWithCommentAddedPost = {},
            updateUserListWithLikedPost = {},
            onIsAnonymousChanged = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}*/

@Preview(showBackground = true)
@Composable
fun PostHeaderPreview() {
    SikshaTheme {
        PostHeader(post = Post(title = "제목", createdAt = LocalDateTime.now(), nickname = "닉네임"), onClick = {})
    }
}

@Preview
@Composable
fun PostLikeButtonPreview() {
    SikshaTheme {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            PostLikeButton(onClick = {}, isLiked = true)
            PostLikeButton(onClick = {}, isLiked = false)
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun CommentItemPreview() {
    SikshaTheme {
        CommentItem(
            comment = Comment(content = "댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 댓글 ", nickname = "유저이름")
        )
    }
}*/

@Preview
@Composable
fun CommentLikeButtonPreview() {
    SikshaTheme {
        CommentLikeButton(
            likeCount = 123,
            isLiked = true,
            onClick = {}
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

@Preview
@Composable
fun CommentInputMultiLinePreview() {
    SikshaTheme {
        CommentInputRow(
            commentInput = "d\nd\nd\nd\nd\nd\nd\nd",
            onCommentInputChanged = {},
            isAnonymous = true,
            onIsAnonymousChanged = {},
            addComment = {}
        )
    }
}
