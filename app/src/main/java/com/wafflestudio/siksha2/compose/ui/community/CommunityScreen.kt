package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.components.compose.Chip
import com.wafflestudio.siksha2.components.compose.PostListItem
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.main.community.CommunityViewModel

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier,
    communityViewModel: CommunityViewModel = hiltViewModel()
) {
    val boards by communityViewModel.boards.collectAsState()
    val postPagingData by communityViewModel.postPagingData.collectAsState()
    val posts = postPagingData.collectAsLazyPagingItems()

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
                        communityViewModel.selectBoard(idx)
                    }
                )
            }
        }
        Divider(color = SikshaColors.Gray400, thickness = 0.5.dp)
        LazyColumn {
            items(
                count = posts.itemCount
            ) {
                posts[it]?.let { post ->
                    PostListItem(
                        title = post.title,
                        content = post.content,
                        likeCount = post.likeCount,
                        commentCount = post.commentCount,
                        isLiked = post.isLiked
                    )
                }
            }
        }
    }
}
