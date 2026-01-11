package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel

// TODO: 사진있는 리뷰에서만 사용하므로 이름 바꿔야 함
@Composable
fun MenuReviewRoute(
    menuId: Long,
    vm: MenuDetailViewModel,
    modifier: Modifier = Modifier
) {
    val reviewsFlow = vm.reviewPhotoPagingData.collectAsLazyPagingItems()

    MenuReviewScreen(
        reviews = reviewsFlow,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun MenuReviewScreen(
    reviews: LazyPagingItems<Review>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            reviews.itemCount,
            key = reviews.itemKey { it.id }
        ) { idx ->
            val review = reviews[idx]
            if (review != null) {
                MenuReviewItem(
                    review.userId.toString(),
                    menuRating = review.score.toFloat(),
                    timeText = review.createdAt,
                    reviewText = review.comment,
                    isLiked = review.isLiked,
                    likeCount = review.likeCount ?: 0L,
                    keywords = review.keywordReviews.filterNotNull().filter { it.isNotBlank() },
                    imageUris = review.etc.images?.map { it.toUri() } ?: listOf(),
                    modifier = Modifier.padding(start = 16.dp, end = 12.dp)
                )
            }
        }
    }
}
