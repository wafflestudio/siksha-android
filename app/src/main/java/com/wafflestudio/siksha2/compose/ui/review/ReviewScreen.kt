package com.wafflestudio.siksha2.compose.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.models.Etc
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.utils.dpToSp

@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
    //menuDetailViewModel: MenuDetailViewModel = hiltViewModel(),
    showImages: Boolean = false
) {
    // val reviews = menuDetailViewModel.getReviews(0).collectAsState(PagingData.empty())

    // temporary reviews
    val reviews = listOf(
        Review(
            id = 0,
            menuId = 0,
            userId = 1234,
            score = 5.0,
            comment = "그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 그냥저냥 먹을만해요 ",
            createdAt = "2023-11-29T09:40:10.322Z",
            etc = Etc(
                images = listOf(
                    "https://postfiles.pstatic.net/MjAyMzExMjZfMjcy/MDAxNzAwOTI3NTczMDY5.c9qVmjXE0nBVZpKukBxB9EB0LytpB5Olc6psLGQdWLQg.-D-zLjlG7bIPYm8XmYzK9-l1vitTZAGcimoHM57QATAg.JPEG.jyurisenpai/20231121203650_1.jpg?type=w773",
                    "https://postfiles.pstatic.net/MjAyMzExMjZfMjU5/MDAxNzAwOTI3MjgxMjAz.f7jMVmS7vYGWKWswaOnnxCjgmaN0qgSt0_2VLB-XZrog.WMa7r-nHh9zw_QXO15bA4ts2NabuiEtQQkM6XYSiA1Ug.JPEG.jyurisenpai/20231118200550_1.jpg?type=w773",
                    "https://postfiles.pstatic.net/MjAyMzExMjZfODAg/MDAxNzAwOTI5MDI4NDE3.kprldbXZmLtHlIh2AFuu9jCeWiXbXeO6pF5OpxpJB3Mg.U8aqpMqPJz4bORV05B8M8oVBF9KXTTJhY1oN17NlkaAg.JPEG.jyurisenpai/20231121211321_1.jpg?type=w773"
                )
            )
        ),
        Review(
            id = 0,
            menuId = 0,
            userId = 1234,
            score = 3.5,
            comment = "그냥저냥 먹을만해요 ",
            createdAt = "2023-11-29T09:40:10.322Z",
            etc = null
        ),
        Review(
            id = 0,
            menuId = 0,
            userId = 1234,
            score = 1.0,
            comment = "맛없어요\n\n\nㅠ",
            createdAt = "2023-11-29T09:40:10.322Z",
            etc = null
        ),
        Review(
            id = 0,
            menuId = 0,
            userId = 1234,
            score = 1.0,
            comment = "맛없어요\n\n\nㅠ",
            createdAt = "2023-11-29T09:40:10.322Z",
            etc = null
        ),
        Review(
            id = 0,
            menuId = 0,
            userId = 1234,
            score = 1.0,
            comment = "맛없어요\n\n\nㅠ",
            createdAt = "2023-11-29T09:40:10.322Z",
            etc = null
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
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
                text = "리뷰",
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .align(Alignment.Center),
                fontSize = dpToSp(20.dp),
                color = SikshaColors.White900
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                //.padding(horizontal = 16.dp)
                .background(color = SikshaColors.White900)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(reviews) { review ->
                ItemReview(
                    review = review,
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


//        item {
//            Text(
//                text = "리뷰가 없습니다.",
//                fontSize = dpToSp(18.dp),
//                //modifier = Modifier.fillMaxSize(),
//                color = SikshaColors.Gray600
//            )
//        }
}

@Preview
@Composable
fun ReviewScreenPreview(){
    ReviewScreen()
}
