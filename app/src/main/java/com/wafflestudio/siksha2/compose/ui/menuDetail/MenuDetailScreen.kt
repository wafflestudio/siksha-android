package com.wafflestudio.siksha2.compose.ui.menuDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.menuDetail.ItemReview
import com.wafflestudio.siksha2.components.compose.menuDetail.LikeButton
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.utils.dpToSp

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

    LaunchedEffect(menuDetailViewModel) {
        menuDetailViewModel.refreshMenu(menuId)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SikshaColors.White900)
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
                    text = "좋아요 " + (menu?.likeCount?:0).toString() + "개",
                    fontSize = dpToSp(14.dp)
                )
            }

            Divider(
                modifier = Modifier.padding(horizontal = 17.dp),
                color = SikshaColors.OrangeMain,
                thickness = 1.dp
            )

//            Column(
//                modifier = Modifier.fillMaxWidth()
//                    .padding(top = 15.dp, bottom = 15.dp, start = 45.dp, end = 25.dp)
//            ) {
//                Row {
//                    Text(
//
//                    )
//                    Text(
//
//                    )
//                    Text(
//
//                    )
//                }
//            }
        }

        Spacer(modifier = Modifier
            .height(10.dp)
            .background(SikshaColors.Gray600))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = SikshaColors.White900)
        ) {
            if(reviews != null) {
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
        }
    }
}
