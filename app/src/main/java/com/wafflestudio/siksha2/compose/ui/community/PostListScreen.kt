package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.Chip
import com.wafflestudio.siksha2.components.compose.PostListItem
import com.wafflestudio.siksha2.ui.NewPostIcon
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.PostListViewModel
import com.wafflestudio.siksha2.ui.main.community.TrendingPostsUiState
import com.wafflestudio.siksha2.utils.DataWithState
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListRoute(
    onClickPost: (Long) -> Unit,
    onNewPost: (Long) -> Unit,
    modifier: Modifier = Modifier,
    postListViewModel: PostListViewModel = hiltViewModel()
) {
    val boards by postListViewModel.boards.collectAsState()
    val selectedBoard by postListViewModel.selectedBoard.collectAsState()
    val posts = postListViewModel.postPagingData.collectAsLazyPagingItems()
    val postListState = postListViewModel.postListState
    val trendingPostsUiState by postListViewModel.trendingPostsUiState.collectAsState()

    PostListScreen(
        boards = boards,
        posts = posts,
        postListState = postListState,
        trendingPostsUiState = trendingPostsUiState,
        onClickPost = onClickPost,
        onClickCreatePost = onNewPost,
        selectedBoard = selectedBoard,
        refreshPosts = {
            postListViewModel.getBoards()
            posts.refresh()
            postListViewModel.invalidateCache()
            postListViewModel.fetchTrendingPosts()
        },
        selectBoard = postListViewModel::selectBoard,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(
    boards: List<DataWithState<Board, Boolean>>,
    posts: LazyPagingItems<Post>,
    postListState: LazyListState,
    trendingPostsUiState: TrendingPostsUiState,
    onClickPost: (Long) -> Unit,
    onClickCreatePost: (Long) -> Unit,
    selectedBoard: Board,
    refreshPosts: () -> Unit,
    selectBoard: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(false, refreshPosts) // 로딩 상태 표시는 PostsLoadingPlaceHolder를 이용하므로 refreshing=false

    Column(
        modifier = modifier
            .background(SikshaColors.White900)
    ) {
        LazyRow(
            modifier = Modifier.padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            itemsIndexed(
                items = boards
            ) { idx, item ->
                Chip(
                    text = item.data.name,
                    selected = item.state,
                    onClick = {
                        selectBoard(idx)
                    }
                )
            }
        }
        Divider(
            color = Color(0xFFF0F0F0),
            thickness = 1.dp
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .pullRefresh(pullRefreshState)
        ) {
            PullRefreshIndicator(
                refreshing = false, // 로딩 상태 표시는 PostsLoadingPlaceHolder를 이용하므로 refreshing=false
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            when (posts.loadState.refresh) {
                is LoadState.NotLoading -> {
                    if (posts.itemCount == 0) {
                        PostsEmptyPlaceHolder(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            state = postListState
                        ) {
                            item {
                                TrendingPostsBanner(
                                    trendingPostsUiState = trendingPostsUiState,
                                    onClickTrendingPost = onClickPost
                                )
                            }
                            items(
                                count = posts.itemCount
                            ) {
                                posts[it]?.let { post ->
                                    PostListItem(
                                        title = post.title,
                                        content = post.content,
                                        likeCount = post.likeCount,
                                        commentCount = post.commentCount,
                                        isLiked = post.isLiked,
                                        onClick = {
                                            onClickPost(post.id)
                                        },
                                        thumbnailImage = post.etc?.images?.first()
                                    )
                                    if (it < posts.itemCount - 1) {
                                        CommunityDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                is LoadState.Loading -> {
                    PostsLoadingPlaceHolder(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LoadState.Error -> {
                    PostsErrorPlaceHolder(
                        onClickRetry = {
                            refreshPosts()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            NewPostIcon(
                modifier = Modifier
                    .padding(end = 30.dp, bottom = 16.dp)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        onClickCreatePost(selectedBoard.id)
                    }
            )
        }
    }
}

@Composable
fun PostsEmptyPlaceHolder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.community_board_empty),
            color = SikshaColors.Gray600,
            style = SikshaTypography.subtitle1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PostsLoadingPlaceHolder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PostsErrorPlaceHolder(
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.community_board_error),
                color = SikshaColors.Gray600,
                style = SikshaTypography.subtitle1
            )
            Button(
                onClick = {
                    onClickRetry()
                }
            ) {
                Text(
                    text = stringResource(R.string.community_retry_button)
                )
            }
        }
    }
}

@Preview(device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Composable
fun PostListScreenPreview() {
    SikshaTheme {
        PostListScreen(
            boards = emptyList(),
            posts = flowOf(PagingData.empty<Post>()).collectAsLazyPagingItems(),
            postListState = LazyListState(0, 0),
            trendingPostsUiState = TrendingPostsUiState.Loading,
            onClickPost = {},
            onClickCreatePost = {},
            selectedBoard = Board(),
            refreshPosts = {},
            selectBoard = {},
            modifier = Modifier
        )
    }
}
