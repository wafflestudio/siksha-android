package com.wafflestudio.siksha2.compose.ui.menuDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.ErrorComponent
import com.wafflestudio.siksha2.components.compose.LoadingComponent
import com.wafflestudio.siksha2.components.compose.menuDetail.ItemRatingBars
import com.wafflestudio.siksha2.components.compose.menuDetail.ItemRatingStars
import com.wafflestudio.siksha2.components.compose.menuDetail.ItemReview
import com.wafflestudio.siksha2.components.compose.menuDetail.LikeButton
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailFragmentDirections
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.utils.dpToSp
import kotlin.math.round

@Composable
fun MenuDetailScreen(
    navController: NavController,
    menuId: Long,
    modifier: Modifier = Modifier,
    menuDetailViewModel: MenuDetailViewModel = hiltViewModel(),
) {
    val menu by menuDetailViewModel.menu.observeAsState()
    val reviewFlow by menuDetailViewModel.reviews.collectAsState()
    val reviews = reviewFlow?.collectAsLazyPagingItems()
    val imageReviewFlow by menuDetailViewModel.reviewsWithImage.collectAsState()
    val imageReviews = imageReviewFlow?.collectAsLazyPagingItems()
    val loadingState = menuDetailViewModel.networkResultState.observeAsState()

    LaunchedEffect(menuDetailViewModel) {
        menuDetailViewModel.refreshMenu(menuId)
        menuDetailViewModel.refreshReviewDistribution(menuId)
    }

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
                        navController.popBackStack()
                    }
            )
            Text(
                text = menu?.nameKr ?: "리뷰",
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .fillMaxWidth(0.72f)
                    .align(Alignment.Center),
                fontSize = dpToSp(20.dp),
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = SikshaColors.White900
            )
        }
                
        when(loadingState.value){
            MenuDetailViewModel.State.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SikshaColors.White900)
                ) {
                    // 상단 별점 정보 + a
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
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
                                    onClick = { menuDetailViewModel.toggleLike() },
                                    modifier = Modifier.size(21.dp)
                                )
                                Text(
                                    text = "좋아요 " + (menu?.likeCount ?: 0).toString() + "개",
                                    fontSize = dpToSp(14.dp)
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
                                        text = "총 ",
                                        color = SikshaColors.Gray800,
                                        fontSize = dpToSp(10.dp)
                                    )
                                    Text(
                                        text = reviews?.itemCount.toString(),
                                        color = SikshaColors.OrangeMain,
                                        fontSize = dpToSp(10.dp)
                                    )
                                    Text(
                                        text = "명",
                                        color = SikshaColors.OrangeMain,
                                        fontSize = dpToSp(10.dp)
                                    )
                                    Text(
                                        text = "이 평가했어요!",
                                        color = SikshaColors.Gray800,
                                        fontSize = dpToSp(10.dp)
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
                                            fontSize = dpToSp(32.dp)
                                        )
                                        ItemRatingStars(
                                            rating = menu?.score?.toFloat() ?: 0.0f
                                        )
                                    }
                                    menuDetailViewModel.reviewDistribution.value?.let {
                                        ItemRatingBars(
                                            distList = it,
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
                                        .clickable { }
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "나의 평가 남기기",
                                        fontSize = dpToSp(14.dp),
                                        color = SikshaColors.White900,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .clickable {
                                                navController.navigate(MenuDetailFragmentDirections.actionMenuDetailFragmentToLeaveReviewFragment())
                                            }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(SikshaColors.Gray100))
                        }
                    }

                    // 사진 리뷰
                    if (imageReviews != null && imageReviews.itemCount > 0) {
                        item{
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "사진 리뷰 모아보기",
                                    fontSize = dpToSp(14.dp),
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                                Image(
                                    painter = painterResource(R.drawable.ic_back_arrow),
                                    contentDescription = "사진 리뷰 모아보기",
                                    colorFilter = ColorFilter.tint(SikshaColors.Gray400),
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .rotate(180f)
                                        .clickable {
                                            navController.navigate(
                                                MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewFragment(
                                                    menuId,
                                                    true
                                                )
                                            )
                                        }
                                )
                            }
                        }
                    }

                    // 일반 리뷰
                    if(reviews != null && reviews.itemCount > 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "리뷰",
                                    fontSize = dpToSp(14.dp),
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
                                            navController.navigate(
                                                MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewFragment(
                                                    menuId,
                                                    false
                                                )
                                            )
                                        }
                                )
                            }
                        }
                        items(reviews.itemCount) {
                            ItemReview(
                                review = reviews[it],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                showImage = true
                            )
                        }
                    }
                    else{
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 36.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "리뷰가 없습니다.",
                                    fontSize = dpToSp(18.dp),
                                    modifier = Modifier.align(Alignment.Center),
                                    color = SikshaColors.Gray600
                                )
                            }
                        }
                    }
                }
            }
            MenuDetailViewModel.State.LOADING -> {
                LoadingComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            MenuDetailViewModel.State.FAILED -> {
                ErrorComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            else -> {}
        }
    }
}
