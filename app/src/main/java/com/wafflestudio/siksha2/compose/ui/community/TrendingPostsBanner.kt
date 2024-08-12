package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.components.compose.LikeIconWithCount
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.TrendingPostsUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrendingPostsBanner(
    trendingPostsUiState: TrendingPostsUiState,
    onClickTrendingPost: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when (trendingPostsUiState) {
        is TrendingPostsUiState.Success -> TrendingPostsBannerSuccess(
            posts = trendingPostsUiState.posts,
            onClickTrendingPost = onClickTrendingPost,
            modifier = modifier
        )
        is TrendingPostsUiState.Loading -> TrendingPostsBannerLoading(modifier = modifier)
        is TrendingPostsUiState.Failed -> Unit
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrendingPostsBannerSuccess(
    posts: List<Post>,
    onClickTrendingPost: (Long) -> Unit,
    modifier: Modifier
) {
    val pagerState = rememberPagerState(pageCount = { posts.size * 1000 }) // Circular 스크롤을 위해 pageCount를 크게 설정한다

    LaunchedEffect(Unit) {
        if (posts.size > 1) {
            while (true) {
                delay(5000L)
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .height(35.dp)
            .fillMaxWidth()
            .background(
                color = SikshaColors.OrangeMain.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClickTrendingPost(posts[pagerState.currentPage % posts.size].id)
            }
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val post = posts[it % posts.size]
                Text(
                    text = post.title,
                    style = SikshaTypography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                LikeIconWithCount(
                    likeCount = post.likeCount,
                    isLiked = false
                )
            }
        }
    }
    CommunityDivider()
}

@Composable
private fun TrendingPostsBannerLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .height(35.dp)
            .fillMaxSize()
            .background(
                color = SikshaColors.OrangeMain.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.Center),
            strokeWidth = 2.dp
        )
    }
    CommunityDivider()
}

@Preview(showBackground = true)
@Composable
fun TrendingPostsBannerSuccessPreview() {
    SikshaTheme {
        TrendingPostsBanner(
            trendingPostsUiState = TrendingPostsUiState.Success(
                List(3) {
                    Post(
                        title = "title${it}title${it}title${it}title${it}title$it",
                        likeCount = it.toLong()
                    )
                }
            ),
            onClickTrendingPost = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrendingPostsBannerLoadingPreview() {
    SikshaTheme {
        TrendingPostsBanner(
            trendingPostsUiState = TrendingPostsUiState.Loading,
            onClickTrendingPost = {}
        )
    }
}
