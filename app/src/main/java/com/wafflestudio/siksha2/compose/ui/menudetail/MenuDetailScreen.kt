package com.wafflestudio.siksha2.compose.ui.menudetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.reviews.MenuReviewImage
import com.wafflestudio.siksha2.compose.ui.reviews.MenuReviewItem
import com.wafflestudio.siksha2.models.KeywordDist
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import kotlin.math.min
import androidx.core.net.toUri
import com.wafflestudio.siksha2.ui.KeywordFoodComposition
import com.wafflestudio.siksha2.ui.KeywordPrice
import com.wafflestudio.siksha2.ui.KeywordTaste
import timber.log.Timber

@Composable
fun MenuDetailRoute(
    menuId: Long,
    vm: MenuDetailViewModel,
    onToggleLikeMenu: () -> Unit,
    onClickLeaveReview: () -> Unit,
    onNavigateToReviewPhoto: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val menu by vm.menu.observeAsState()
    val reviews = vm.getReviews(menuId).collectAsLazyPagingItems()
    val imageReviews = vm.getReviewsWithImages(menuId).collectAsLazyPagingItems()
    val keywordDist by vm.keywordDistribution.observeAsState()

    MenuDetailScreen(
        menu = menu,
        reviews = reviews,
        imageReviews = imageReviews,
        keywordDist = keywordDist ?: KeywordDist.Empty,
        onToggleLikeMenu = onToggleLikeMenu,
        onClickLeaveReview = onClickLeaveReview,
        onNavigateToReviewPhoto = { menu?.let { onNavigateToReviewPhoto(it.id) } },
        modifier = modifier
    )
}

@Composable
fun MenuDetailScreen(
    menu: Menu?,
    reviews: LazyPagingItems<Review>,
    imageReviews: LazyPagingItems<Review>,
    keywordDist: KeywordDist,
    onToggleLikeMenu: () -> Unit,
    onClickLeaveReview: () -> Unit,
    onNavigateToReviewPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(reviews) {
        snapshotFlow { reviews.itemCount }
            .collect { count ->
                Timber.d("Item count changed: $count")
            }
    }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(SikshaTheme.colors.BackgroundPrimary)
                    .padding(top = 20.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(if (menu?.isLiked ?: false) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                        .clickable { onToggleLikeMenu() }
                )
                Text(
                    text = "찜 ${(menu?.likeCount ?: 0)}개",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SikshaTheme.colors.Black
                )
            }
        }
        item {
            Spacer(Modifier.height(10.dp).fillMaxWidth().background(SikshaTheme.colors.Gray100))
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(SikshaTheme.colors.BackgroundPrimary)
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    MenuRatingsInfo(
                        rating = menu?.score?.toFloat() ?: 0f,
                        reviewCount = menu?.reviewCount?.toInt() ?: 0,
                        modifier = Modifier.fillMaxHeight()
                    )
                    Spacer(Modifier.width(12.dp))
                    MenuKeywordStats(
                        keywordDist.keywords,
                        keywordDist.keywordCounts,
                        keywordIcons = listOf(
                            { KeywordTaste() },
                            { KeywordPrice() },
                            { KeywordFoodComposition() }
                        ),
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                    )
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.leave_review_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SikshaTheme.colors.TextButton,
                    modifier = Modifier.background(
                        color = SikshaTheme.colors.Orange500,
                        shape = RoundedCornerShape(50.dp)
                    )
                        .clickable { onClickLeaveReview() }
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                )
            }
        }
        item {
            Spacer(Modifier.height(10.dp).fillMaxWidth().background(SikshaTheme.colors.Gray100))
        }
        item {
            BriefImageReviews(
                imageReviews = imageReviews,
                onNavigateToReviewPhoto = onNavigateToReviewPhoto
            )
        }
        item {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.5.dp, vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.menu_detail_review_gather),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = SikshaTheme.colors.Black
                )
            }
        }
        items(
            reviews.itemCount,
            key = reviews.itemKey { it.id }
        ) { idx ->
            val review = reviews[idx]
            if (review != null) {
                MenuReviewItem(
                    userName = review.userId.toString(),
                    menuRating = review.score.toFloat(),
                    timeText = review.createdAt,
                    reviewText = review.comment,
                    isLiked = review.isLiked,
                    likeCount = review.likeCount,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }
        }
    }
}

@Composable
fun BriefImageReviews(
    imageReviews: LazyPagingItems<Review>,
    onNavigateToReviewPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePreviewScrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.5.dp, vertical = 14.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_detail_photo_review_gather),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterStart),
            color = SikshaTheme.colors.Black
        )
        Image(
            painter = painterResource(R.drawable.ic_back_arrow),
            contentDescription = stringResource(R.string.menu_detail_photo_review_gather),
            colorFilter = ColorFilter.tint(SikshaTheme.colors.Gray600),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .rotate(180f)
                .clickable {
                    onNavigateToReviewPhoto()
                }
        )
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .horizontalScroll(imagePreviewScrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i: Int in 1..min(imageReviews.itemCount, 3)) {
            if (imageReviews.itemSnapshotList.items[i - 1].etc.isNotEmpty()) {
                val it = imageReviews.itemSnapshotList.items[i - 1].etc[0]
                if (i == 3) {
                    MenuDetailImagesShowMore(
                        imageUri = it.toUri(),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        showMoreCount = imageReviews.itemCount - 2,
                        onShowMore = {
                            onNavigateToReviewPhoto()
                        }
                    )
                } else {
                    MenuReviewImage(
                        imageUri = it.toUri(),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SikshaTheme.colors.Gray100)
                )
            }
        }
    }
}
