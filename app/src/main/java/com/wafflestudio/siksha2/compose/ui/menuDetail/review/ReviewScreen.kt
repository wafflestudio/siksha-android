package com.wafflestudio.siksha2.compose.ui.menuDetail.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.NanumSquareFontFamily
import com.wafflestudio.siksha2.components.compose.menuDetail.ItemReview
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.utils.dpToSp

@Composable
fun ReviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    menuDetailViewModel: MenuDetailViewModel = hiltViewModel(),
    showImages: Boolean = false
) {
    val reviewFlow by
    if (!showImages) {
        menuDetailViewModel.reviews.collectAsState()
    } else {
        menuDetailViewModel.reviewsWithImage.collectAsState()
    }
    val reviews = reviewFlow?.collectAsLazyPagingItems()

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
                text = "리뷰",
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .align(Alignment.Center),
                fontSize = dpToSp(20.dp),
                fontFamily = NanumSquareFontFamily,
                fontWeight = FontWeight.Bold,
                color = SikshaColors.White900
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (reviews == null || reviews.itemCount == 0) {
                Text(
                    text = "리뷰가 없습니다.",
                    fontSize = dpToSp(18.dp),
                    fontFamily = NanumSquareFontFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.Center),
                    color = SikshaColors.Gray600
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = SikshaColors.White900)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(reviews.itemCount) {
                        ItemReview(
                            review = reviews[it],
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            showImage = showImages
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
