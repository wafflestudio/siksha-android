package com.wafflestudio.siksha2.compose.ui.reviews

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.compose.ui.menudetail.MenuRatingStars
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.ui.KeywordFoodComposition
import com.wafflestudio.siksha2.ui.KeywordPrice
import com.wafflestudio.siksha2.ui.KeywordTaste
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.utils.KeyboardUtil.keyboardAsState
import com.wafflestudio.siksha2.utils.hasFinalConsInKr
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LeaveReviewRoute(
    keywordTitleList: List<String>,
    keywordChoiceLists: List<List<String>>,
    vm: MenuDetailViewModel,
    onNavigateUp: () -> Unit,
    onAddImage: () -> Unit,
    onClickDetails: (Uri) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    val menu by vm.menu.observeAsState()
    val rating by vm.reviewRating
    val selectedKeywords by vm.selectedKeywordList.collectAsState()
    val commentHint by vm.commentHint.observeAsState()
    val imageUriList by vm.imageUriList.observeAsState()
    val comment by vm.comment.collectAsState()

    val keyboardState by keyboardAsState()

    val scope = rememberCoroutineScope()

    LeaveReviewScreen(
        menu = menu,
        rating = rating,
        submitEnabled = selectedKeywords.all { it != "" },
        keywordTitleList = keywordTitleList,
        keywordChoiceLists = keywordChoiceLists,
        keywordIconList = listOf({ KeywordTaste() }, { KeywordPrice() }, { KeywordFoodComposition() }),
        selectedKeywords = selectedKeywords,
        comment = comment,
        commentPlaceHolder = {
            Text(
                text = commentHint ?: "",
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = SikshaTheme.colors.Gray600
            )
        },
        isKeyboardOpen = keyboardState,
        imageUriList = imageUriList,
        onNavigateUp = onNavigateUp,
        onSubmitReview = {
            scope.launch { vm.leaveReview(context) }
        },
        onRatingChange = { rating -> vm.setReviewRating(rating) },
        onSelectKeyword = { idx, keyword -> vm.selectKeyword(idx, keyword) },
        onCommentChange = { text ->
            if (text.length <= 150) vm.setComment(text)
        },
        onAddImage = onAddImage,
        onClickDetails = onClickDetails,
        onDeleteImage = { vm.deleteImageUri(it) },
        modifier = modifier
    )
}

@Composable
fun LeaveReviewScreen(
    menu: Menu?,
    rating: Float,
    submitEnabled: Boolean,
    keywordTitleList: List<String>,
    keywordChoiceLists: List<List<String>>,
    keywordIconList: List<@Composable () -> Unit>,
    selectedKeywords: List<String>,
    comment: String,
    commentPlaceHolder: @Composable () -> Unit,
    isKeyboardOpen: Boolean,
    imageUriList: List<Uri>?,
    onNavigateUp: () -> Unit,
    onSubmitReview: () -> Unit,
    onRatingChange: (Float) -> Unit,
    onSelectKeyword: (Int, String) -> Unit,
    onCommentChange: (String) -> Unit,
    onAddImage: () -> Unit,
    onClickDetails: (Uri) -> Unit,
    onDeleteImage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize()
            .background(SikshaTheme.colors.BackgroundPrimary)
            .imePadding()
    ) {
        TopBar(
            title = stringResource(R.string.leave_review_title),
            navigationButton = {
                Image(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onNavigateUp() }
                )
            }
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        text = menu?.nameKr ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SikshaTheme.colors.Black
                    )
                    Text(
                        text = stringResource(
                            when (menu?.nameKr?.hasFinalConsInKr()) {
                                true -> R.string.leave_review_how_about_with_bottom
                                else -> R.string.leave_review_how_about_wo_bottom
                            }
                        ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SikshaTheme.colors.Gray700
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.leave_review_star_description),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SikshaTheme.colors.Gray700
                )
                Spacer(Modifier.height(9.dp))
                MenuRatingStars(
                    initialRating = 5f,
                    changeEnabled = true,
                    onRatingChange = onRatingChange,
                    width = 150.dp,
                    height = 25.dp
                )
                Spacer(Modifier.height(9.dp))
                Text(
                    text = rating.roundToInt().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SikshaTheme.colors.Black
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.leave_review_keyword_description),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SikshaTheme.colors.Black
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "(필수)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SikshaTheme.colors.Gray700
                )
            }
            Spacer(Modifier.height(18.dp))

            keywordTitleList.forEachIndexed { idx, keywordTitle ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        keywordIconList[idx]()
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = keywordTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SikshaTheme.colors.Black
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    LeaveReviewKeywordChips(
                        keywordChoiceLists[idx],
                        selectedKeyword = selectedKeywords[idx],
                        selectKeywordFromList = { keyword ->
                            onSelectKeyword(idx, keyword)
                        }
                    )
                    if (idx == keywordTitleList.size) {
                        Spacer(Modifier.height(22.dp))
                    } else {
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_text_bubble),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.leave_review_description),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SikshaTheme.colors.Black
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "(선택)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SikshaTheme.colors.Gray700
                )
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        color = SikshaTheme.colors.Gray50,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                BasicTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 12.dp, start = 14.dp, end = 16.dp),
                    singleLine = false,
                    textStyle = SikshaTypography.body1.copy(color = SikshaTheme.colors.Black, fontSize = 14.sp, lineHeight = 21.sp),
                    cursorBrush = SolidColor(SikshaTheme.colors.Orange500),
                    decorationBox = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 110.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            it()
                            if (comment.isEmpty()) {
                                commentPlaceHolder()
                            }
                        }
                    }
                )
                Text(
                    text = stringResource(R.string.leave_review_text_count, comment.length, 150),
                    fontSize = 11.sp,
                    color = SikshaTheme.colors.Gray700,
                    modifier = Modifier.align(Alignment.End)
                        .padding(end = 11.dp, bottom = 12.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            MenuReviewImagesEdit(
                uris = imageUriList ?: listOf(),
                onAddImage = onAddImage,
                onClickDetails = onClickDetails,
                onDeleteImage = onDeleteImage,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!isKeyboardOpen) {
            Text(
                text = stringResource(R.string.leave_review_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = SikshaTheme.colors.TextButton,
                modifier = Modifier.fillMaxWidth()
                    .background(SikshaTheme.colors.BackgroundPrimary)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 45.dp)
                    .background(
                        color = if (submitEnabled) SikshaTheme.colors.Orange500 else SikshaTheme.colors.Gray600,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (submitEnabled) onSubmitReview()
                    }
                    .padding(top = 16.dp, bottom = 15.dp)
            )
        }
    }
}

@Preview
@Composable
fun LeaveButtonPreview() {
    Text(
        text = stringResource(R.string.leave_review_button),
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = SikshaTheme.colors.TextButton,
        modifier = Modifier.fillMaxWidth()
            .background(SikshaTheme.colors.BackgroundPrimary)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 45.dp)
            .background(
                color = SikshaTheme.colors.Gray600,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 16.dp, bottom = 15.dp)
    )
}
