package com.wafflestudio.siksha2.compose.ui.reviews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    val reviewsflow = vm.getReviews(menuId).collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            reviewsflow.itemCount,
            key = reviewsflow.itemKey { it.id }
        ) { idx ->
            val review = reviewsflow[idx]
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

@Composable
fun MenuReviewScreen(
    reviews: List<Review>
) {
    LazyColumn {
        items(reviews) { review ->
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
