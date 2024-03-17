package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState.Companion.Saver
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.Chip
import com.wafflestudio.siksha2.components.compose.PostListItem
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.PostListViewModel

@Composable
fun PostListScreen(
    onClickPost: (Long) -> Unit,
    modifier: Modifier = Modifier,
    postListViewModel: PostListViewModel = hiltViewModel()
) {
    val boards by postListViewModel.boards.collectAsState()
    val postPagingData by postListViewModel.postPagingData.collectAsState()
    val posts = postPagingData.collectAsLazyPagingItems()
    val postListState = rememberSaveable(saver = Saver) {
        postListViewModel.postListState
    }

    Column(
        modifier = modifier.background(SikshaColors.White900)
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            itemsIndexed(
                items = boards
            ) { idx, item ->
                Chip(
                    text = item.data.name,
                    selected = item.state,
                    onClick = {
                        postListViewModel.selectBoard(idx)
                    }
                )
            }
        }
        Divider(color = SikshaColors.Gray400, thickness = 0.5.dp)
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
                                    }
                                )
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
