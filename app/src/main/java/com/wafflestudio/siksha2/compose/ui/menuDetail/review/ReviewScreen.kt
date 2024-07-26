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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.components.compose.menuDetail.MenuReview
import com.wafflestudio.siksha2.models.Comment
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.utils.dpToSp
import kotlinx.coroutines.flow.flowOf

@Composable
fun ReviewRoute(
    showImages: Boolean,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    menuDetailViewModel: MenuDetailViewModel = hiltViewModel(),
) {
    val reviews = if (!showImages) {
        menuDetailViewModel.reviews.collectAsLazyPagingItems()
    } else {
        menuDetailViewModel.reviewsWithImage.collectAsLazyPagingItems()
    }

    ReviewScreen(
        showImages = showImages,
        reviews = reviews,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@Composable
fun ReviewScreen(
    showImages: Boolean,
    reviews: LazyPagingItems<Review>,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopBar(
            title = stringResource(R.string.review_title),
            textStyle = MaterialTheme.typography.subtitle1.copy(
                fontSize = dpToSp(20.dp),
            ),
            navigationButton = {
                Image(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .clickable {
                            onNavigateUp()
                        }
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (reviews.itemCount == 0) {
                Text(
                    text = stringResource(R.string.review_nothing),
                    fontSize = dpToSp(18.dp),
                    fontWeight = FontWeight.Medium,
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
                        reviews[it]?.let { review ->
                            MenuReview(
                                review = review,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                showImage = showImages
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    SikshaTheme {
        ReviewScreen(
            showImages = false,
            reviews = flowOf(PagingData.empty<Review>()).collectAsLazyPagingItems(),
            onNavigateUp = {}
        )
    }
}
