package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.ui.CancelIcon
import com.wafflestudio.siksha2.ui.ExpandOptionsIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.CreatePostViewModel
import java.time.format.TextStyle

@Composable
fun CreatePostRoute(
    modifier: Modifier = Modifier,
    createPostViewModel: CreatePostViewModel = hiltViewModel()
) {

}

@Composable
fun CreatePostScreen(
    currentBoard: Board,
    onNavigateUp: () -> Unit,
    onOpenBoardList: () -> Unit,
    titleTextValue: String,
    onTitleTextChanged: (String) -> Unit,
    contentTextValue: String,
    onContentTextChanged: (String) -> Unit,
    isAnonymous: Boolean,
    onIsAnonymousChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .background(SikshaColors.White900)
    ) {
        TopBar(
            title = "글쓰기",
            navigationButton = {
                CancelIcon(
                    modifier = Modifier.clickable {
                        onNavigateUp()
                    }
                )
            }
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            CurrentBoard(
                board = currentBoard,
                onClick = onOpenBoardList
            )
            Spacer(modifier = Modifier.height(6.dp))
            TitleEditText(
                value = titleTextValue,
                onValueChange = onTitleTextChanged,
                // TODO: placeholder
            )
            Spacer(modifier = Modifier.height(14.dp))
            ContentEditText(
                value = contentTextValue,
                onValueChange = onContentTextChanged,
                // TODO: placeholder
            )
            Spacer(modifier = Modifier.height(35.dp))
            AnonymousCheckbox(
                isAnonymous = isAnonymous,
                onIsAnonymousChanged = onIsAnonymousChanged,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

@Composable
fun CurrentBoard(
    board: Board,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = SikshaColors.Gray350,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(top = 11.dp, bottom = 10.dp)
            .clickable {
                onClick()
            }
    ) {
        Row (
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = board.name,
                color = SikshaColors.Gray600
            )
            Spacer(modifier = Modifier.width(18.dp))
            ExpandOptionsIcon(
                color = SikshaColors.Gray500
            )
        }
    }
}

@Composable
fun BoardSelector(
    modifier: Modifier = Modifier
){

}

@Composable
fun TitleEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = SikshaTypography.subtitle1,
        decorationBox = {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SikshaColors.Gray100,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(
                        top = 11.dp, bottom = 8.dp, start = 12.dp, end = 12.dp
                    )
            ){
                it()
                placeholder()
            }
        }
    )
}

@Composable
fun ContentEditText (
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        decorationBox = {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 110.dp)
            ){
                it()
                placeholder()
            }
        }
    )
}

@Composable
fun AnonymousCheckbox(
    isAnonymous: Boolean,
    onIsAnonymousChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Checkbox(
//            checked = isAnonymous,
//            onCheckedChange = onIsAnonymousChanged,
//            modifier = Modifier.size(13.dp)
//        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "익명",
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (isAnonymous) SikshaColors.OrangeMain else SikshaColors.Gray400
        )
    }
}

@Composable
fun EditPhoto (

) {

}

@Preview
@Composable
fun CreatePostScreenPreview(){
    SikshaTheme {
        CreatePostScreen(
            currentBoard = Board(
                name = "자유게시판"
            ),
            onNavigateUp = { },
            onOpenBoardList = { },
            titleTextValue = "제목",
            onTitleTextChanged = { },
            contentTextValue = "나는 아무 걱정도 없이 가을 속의 별들을 다 헬 듯합니다. 계절이 지나가는 하늘에는 가을로 가득 차 있습니다. 어머님, 그리고 당신은 멀리 북간도에 계십니다. 어머님, 그리고 당신은 멀리 북간도에 계십니다. 그러나, 겨울이 지나고 나의 별에도 봄이 오면, 무덤 위에 파란 잔디가 피어나듯이 내 이름자 묻힌 언덕 위에도 자랑처럼 풀이 무성할 거외다. 나는 아무 걱정도 없이 가을 속의 별들을 다 헬 듯합니다.",
            onContentTextChanged = { },
            isAnonymous = true,
            onIsAnonymousChanged = { }
        )
    }
}
