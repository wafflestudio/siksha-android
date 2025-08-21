package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.components.compose.PostListItem
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.UserPostListViewModel
import com.wafflestudio.siksha2.ui.NavigateUpIcon

@Composable
fun UserPostListRoute(
    onNavigateUp: () -> Unit,
    onClickPost: (Long) -> Unit,
    modifier: Modifier = Modifier,
    userPostListViewModel: UserPostListViewModel = hiltViewModel()
) {
    val posts = userPostListViewModel.postPagingData.collectAsLazyPagingItems()
    val postListState = userPostListViewModel.postListState

    UserPostListScreen(
        posts = posts,
        postListState = postListState,
        onClickPost = onClickPost,
        refreshPosts = {
            posts.refresh()
            userPostListViewModel.invalidateCache()
        },
        modifier = modifier,
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserPostListScreen(
    posts: LazyPagingItems<Post>,
    postListState: LazyListState,
    onNavigateUp: () -> Unit,
    onClickPost: (Long) -> Unit,
    refreshPosts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(false, refreshPosts) // 로딩 상태 표시는 PostsLoadingPlaceHolder를 이용하므로 refreshing=false

    Column(
        modifier = modifier
            .background(SikshaColors.White900)
    ) {
        TopBar(
            title = "내가 쓴 글",
            navigationButton = {
                NavigateUpIcon(
                    modifier = Modifier.clickable {
                        onNavigateUp()
                    }
                )
            }
        )
        Box(
            modifier = Modifier
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
                        UserPostsEmptyPlaceHolder(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            state = postListState
                        ) {
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
                            posts.refresh()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun UserPostsEmptyPlaceHolder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = "아직 작성한 글이 없어요.",
            color = SikshaColors.Gray600,
            style = SikshaTypography.subtitle1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
