package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel

@Composable
fun MenuReviewRoute(
    menuId: Long,
    vm: MenuDetailViewModel,
    modifier: Modifier = Modifier
) {
    val reviewsFlow = vm.getReviews(menuId).collectAsLazyPagingItems()

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
                    likeCount = review.likedCount
                )
            }
        }
    }
}
