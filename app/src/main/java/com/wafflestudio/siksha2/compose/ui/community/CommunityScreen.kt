package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.Chip
import com.wafflestudio.siksha2.components.compose.PostListItem
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.main.community.CommunityViewModel

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier
) {
    val communityViewModel: CommunityViewModel = hiltViewModel()
    Column(
        modifier = modifier.background(SikshaColors.White900)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Chip(
                text = stringResource(R.string.community_board_free),
                selected = true
            )
            Chip(
                text = stringResource(R.string.community_board_review),
                selected = false
            )
        }
        Divider(color = SikshaColors.Gray400, thickness = 0.5.dp)
        LazyColumn {
            items(
                items = communityViewModel.dummyPosts
            ) {
                PostListItem(
                    title = it.title,
                    content = it.content,
                    likeCount = it.likeCount,
                    commentCount = it.commentCount,
                    isLiked = it.isLiked
                )
            }
        }
    }
}
