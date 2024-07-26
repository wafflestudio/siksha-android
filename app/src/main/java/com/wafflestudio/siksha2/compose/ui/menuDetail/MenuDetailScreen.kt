package com.wafflestudio.siksha2.compose.ui.menuDetail

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.ErrorPlaceHolder
import com.wafflestudio.siksha2.components.compose.LoadingPlaceHolder
import com.wafflestudio.siksha2.components.compose.menuDetail.LikeButton
import com.wafflestudio.siksha2.components.compose.menuDetail.MenuRatingBars
import com.wafflestudio.siksha2.components.compose.menuDetail.MenuRatingStars
import com.wafflestudio.siksha2.components.compose.menuDetail.MenuReview
import com.wafflestudio.siksha2.components.compose.menuDetail.MenuReviewImage
import com.wafflestudio.siksha2.components.compose.menuDetail.MenuReviewImageShowMore
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.ui.menuDetail.MenuLoadingState
import com.wafflestudio.siksha2.utils.dpToSp
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import kotlin.math.min
import kotlin.math.round

@Composable
fun MenuDetailRoute(
    menuId: Long,
    isTodayMenu: Boolean,
    onNavigateUp: () -> Unit,
    onNavigateToLeaveReview: () -> Unit,
    onNavigateToReviewPhoto: (Long) -> Unit,
    onNavigateToReview: (Long) -> Unit,
    modifier: Modifier = Modifier,
    menuDetailViewModel: MenuDetailViewModel = hiltViewModel()
) {
    val menu by menuDetailViewModel.menu.observeAsState()   // todo: LiveData대신 StateFlow 써서 non-null로 만들기
    val reviewDistribution by menuDetailViewModel.reviewDistribution.observeAsState()
    val reviews = menuDetailViewModel.reviews.collectAsLazyPagingItems()
    val imageReviews = menuDetailViewModel.reviewsWithImage.collectAsLazyPagingItems()
    val loadingState by menuDetailViewModel.networkResultMenuLoadingState.observeAsState()

    LaunchedEffect(Unit) {
        menuDetailViewModel.refreshMenu(menuId)
        menuDetailViewModel.refreshReviewDistribution(menuId)
    }

    MenuDetailScreen(
        menu = menu,
        reviewDistribution = reviewDistribution,
        reviews = reviews,
        imageReviews = imageReviews,
        loadingState = loadingState,
        isTodayMenu = isTodayMenu,
        onClickLike = { menuDetailViewModel.toggleLike() },
        onNavigateUp = onNavigateUp,
        onNavigateToLeaveReview = onNavigateToLeaveReview,
        onNavigateToReviewPhoto = onNavigateToReviewPhoto,
        onNavigateToReview = onNavigateToReview,
        modifier = modifier,
    )
}

@Composable
fun MenuDetailScreen(
    menu: Menu?,
    reviewDistribution: List<Long>?,
    reviews: LazyPagingItems<Review>,
    imageReviews: LazyPagingItems<Review>,
    loadingState: MenuLoadingState?,
    isTodayMenu: Boolean,
    onClickLike: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToLeaveReview: () -> Unit,
    onNavigateToReviewPhoto: (Long) -> Unit,
    onNavigateToReview: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color = SikshaColors.OrangeMain)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        onNavigateUp()
                    }
            )
            Text(
                text = menu?.nameKr ?: stringResource(R.string.review_title),
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .fillMaxWidth(0.72f)
                    .align(Alignment.Center),
                fontSize = dpToSp(20.dp),
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = SikshaColors.White900
            )
        }

        when (loadingState) {
            MenuLoadingState.SUCCESS -> {
                MenuDetailContent(
                    menu = menu,
                    reviewDistribution = reviewDistribution,
                    reviews = reviews,
                    imageReviews = imageReviews,
                    isTodayMenu = isTodayMenu,
                    onClickLike = onClickLike,
                    onNavigateToLeaveReview = onNavigateToLeaveReview,
                    onNavigateToReviewPhoto = onNavigateToReviewPhoto,
                    onNavigateToReview = onNavigateToReview
                )
            }

            MenuLoadingState.LOADING -> {
                LoadingPlaceHolder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            MenuLoadingState.FAILED -> {
                ErrorPlaceHolder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            else -> {}
        }
    }
}

@Composable
fun MenuDetailContent(
    menu: Menu?,
    reviewDistribution: List<Long>?,
    reviews: LazyPagingItems<Review>,
    imageReviews: LazyPagingItems<Review>,
    isTodayMenu: Boolean,
    onClickLike: () -> Unit,
    onNavigateToLeaveReview: () -> Unit,
    onNavigateToReviewPhoto: (Long) -> Unit,
    onNavigateToReview: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(SikshaColors.White900)
    ) {
        // 상단 별점 정보 + a
        item {
            MenuStatistics(
                menu = menu,
                reviewDistribution = reviewDistribution,
                reviews = reviews,
                onClickLike = onClickLike,
                onLeaveReview = {
                    if (isTodayMenu) {
                        onNavigateToLeaveReview()
                    } else {
                        Toast
                            .makeText(
                                context,
                                "오늘 메뉴만 평가할 수 있습니다.",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            )
        }

        // 사진 리뷰
        if (imageReviews.itemCount > 0) {
            item {
                BriefImageReviews(
                    menu = menu,
                    imageReviews = imageReviews,
                    onNavigateToReviewPhoto = onNavigateToReviewPhoto
                )
            }
        }

        // 일반 리뷰
        if (reviews.itemCount > 0) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.menu_detail_review_gather),
                        fontSize = dpToSp(14.dp),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = "리뷰",
                        colorFilter = ColorFilter.tint(SikshaColors.Gray400),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .rotate(180f)
                            .clickable {
                                menu?.let {
                                    onNavigateToReview(it.id)
                                }
                            }
                    )
                }
            }
            items(reviews.itemCount) {
                reviews[it]?.let { review ->
                    MenuReview(
                        review = review,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        showImage = true
                    )
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 36.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.review_nothing),
                        fontSize = dpToSp(18.dp),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.align(Alignment.Center),
                        color = SikshaColors.Gray600
                    )
                }
            }
        }
    }
}

@Composable
fun MenuStatistics(
    menu: Menu?,
    reviewDistribution: List<Long>?,
    reviews: LazyPagingItems<Review>,
    onClickLike: () -> Unit,
    onLeaveReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LikeButton(
                isChecked = menu?.isLiked ?: false,
                onClick = onClickLike,
                modifier = Modifier.size(21.dp)
            )
            Text(
                text = stringResource(R.string.review_like_prefix) +
                    (menu?.likeCount ?: 0).toString() +
                    stringResource(R.string.review_like_suffix),
                fontSize = dpToSp(14.dp),
                fontWeight = FontWeight.Medium
            )
        }

        Divider(
            modifier = Modifier.padding(horizontal = 17.dp),
            color = SikshaColors.OrangeMain,
            thickness = 1.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 15.dp,
                    bottom = 15.dp,
                    start = 45.dp,
                    end = 25.dp
                )
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.review_count_prefix),
                    color = SikshaColors.Gray800,
                    fontSize = dpToSp(10.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reviews.itemCount.toString(),
                    color = SikshaColors.OrangeMain,
                    fontSize = dpToSp(10.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.review_count_suffix_orange),
                    color = SikshaColors.OrangeMain,
                    fontSize = dpToSp(10.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.review_count_suffix_black),
                    color = SikshaColors.Gray800,
                    fontSize = dpToSp(10.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${
                            menu?.score?.times(10)?.let { round(it) / 10 } ?: "0.0"
                        }",
                        fontSize = dpToSp(32.dp),
                        fontWeight = FontWeight.ExtraBold
                    )
                    MenuRatingStars(
                        rating = menu?.score?.toFloat() ?: 0.0f
                    )
                }
                reviewDistribution?.let {
                    MenuRatingBars(
                        distributions = it,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(32.dp)
                    .background(
                        color = SikshaColors.OrangeMain,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clickable {
                        onLeaveReview()
                    }
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.menu_detail_leave_review_button),
                    fontSize = dpToSp(14.dp),
                    fontWeight = FontWeight.ExtraBold,
                    color = SikshaColors.White900,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(SikshaColors.Gray100)
        )
    }
}

@Composable
fun BriefImageReviews(
    menu: Menu?,
    imageReviews: LazyPagingItems<Review>,
    onNavigateToReviewPhoto: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePreviewScrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_detail_photo_review_gather),
            fontSize = dpToSp(14.dp),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Image(
            painter = painterResource(R.drawable.ic_back_arrow),
            contentDescription = stringResource(R.string.menu_detail_photo_review_gather),
            colorFilter = ColorFilter.tint(SikshaColors.Gray400),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .rotate(180f)
                .clickable {
                    menu?.let {
                        onNavigateToReviewPhoto(it.id)
                    }
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
            when (val it = imageReviews.itemSnapshotList.items[i - 1].etc?.images?.get(0)) {
                null -> Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(SikshaColors.Gray100)
                        .clip(RoundedCornerShape(10.dp))
                )

                else -> {
                    if (i == 3) {
                        MenuReviewImageShowMore(
                            imageUri = Uri.parse(it),
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            showMoreCount = imageReviews.itemCount - 2,
                            onShowMore = {
                                menu?.let {
                                    onNavigateToReviewPhoto(it.id)
                                }
                            }
                        )
                    } else {
                        MenuReviewImage(
                            imageUri = Uri.parse(it),
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }
            }
        }
    }
}

private val testMenu = Menu(
    id = 0L,
    code = "",
    date = LocalDate.now(),
    type = MealsOfDay.BR,
    restaurantId = 0L,
    nameKr = "nameKr",
    nameEn = "nameEn",
    price = 10000L,
    score = 3.39,
    etc = listOf("https://picsum.photos/200", "https://picsum.photos/201"),
    reviewCount = 10L,
    isLiked = true,
    likeCount = 100L,
)

@Preview
@Composable
fun MenuDetailScreenPreview() {
    SikshaTheme {
        MenuDetailScreen(
            menu = testMenu,
            reviewDistribution = listOf(1L, 2L, 3L, 4L, 5L),
            reviews = flowOf(PagingData.empty<Review>()).collectAsLazyPagingItems(),
            imageReviews = flowOf(PagingData.empty<Review>()).collectAsLazyPagingItems(),
            loadingState = MenuLoadingState.SUCCESS,
            isTodayMenu = true,
            onClickLike = {},
            onNavigateUp = {},
            onNavigateToLeaveReview = {},
            onNavigateToReviewPhoto = {},
            onNavigateToReview = {},
            modifier = Modifier
        )
    }
}
