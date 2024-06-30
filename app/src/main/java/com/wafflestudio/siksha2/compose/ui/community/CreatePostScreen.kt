package com.wafflestudio.siksha2.compose.ui.community

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.ui.AddPostImageIcon
import com.wafflestudio.siksha2.ui.CancelIcon
import com.wafflestudio.siksha2.ui.DeletePostImageIcon
import com.wafflestudio.siksha2.ui.ExpandOptionsIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.CreatePostViewModel

@Composable
fun CreatePostRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    createPostViewModel: CreatePostViewModel = hiltViewModel()
) {
    val board by createPostViewModel.board.collectAsState()
    val imageUriList by createPostViewModel.imageUriList.collectAsState()

    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if (it != null) createPostViewModel.addImageUri(it)
    }

    CreatePostScreen(
        currentBoard = board,
        onNavigateUp = onNavigateUp,
        onOpenBoardList = { },  // TODO
        titleTextValue = titleInput,
        onTitleTextChanged = {
            if (titleInput.length < 200) titleInput = it
            // TODO: 길이 제한 토스트 출력
        },
        contentTextValue = contentInput,
        onContentTextChanged = {
            if (contentInput.length < 200) contentInput = it
            // TODO: 길이 제한 토스트 출력
        },
        isAnonymous = isAnonymous,
        onIsAnonymousChanged = { isAnonymous = it },
        imageUriList = imageUriList,
        onDeleteImage = { idx ->
            createPostViewModel.deleteImageUri(idx)
        },
        onAddImage = { launcher.launch("image/*") },
        onUpload = { createPostViewModel.createPost(context, titleInput, contentInput, isAnonymous) },
        isUploadActivated = (titleInput.isNotEmpty()) && (contentInput.isNotEmpty()),
        modifier = modifier
    )
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
    imageUriList: List<Uri>,
    onDeleteImage: (Int) -> Unit,
    onAddImage: () -> Unit,
    onUpload: () -> Unit,
    isUploadActivated: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                CurrentBoard(
                    board = currentBoard,
                    onClick = onOpenBoardList
                )
                Spacer(modifier = Modifier.height(6.dp))
                TitleEditText(
                    value = titleTextValue,
                    onValueChange = onTitleTextChanged,
                    placeholder = {
                        Text(
                            text = "제목",
                            color = SikshaColors.Gray400,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                ContentEditText(
                    value = contentTextValue,
                    onValueChange = onContentTextChanged,
                    placeholder = {
                        Text(
                            text = "내용을 입력하세요.",
                            color = SikshaColors.Gray400
                        )
                    }
                )
                Spacer(modifier = Modifier.height(35.dp))
                AnonymousCheckbox(
                    isAnonymous = isAnonymous,
                    onIsAnonymousChanged = onIsAnonymousChanged,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = SikshaColors.Gray100, thickness = 1.dp)
                Spacer(modifier = Modifier.height(6.dp))
                EditImage(
                    imageUriList = imageUriList,
                    onDeleteImage = onDeleteImage,
                    onAddImage = onAddImage
                )
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = if (isUploadActivated) SikshaColors.OrangeMain else SikshaColors.Gray500,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .align(Alignment.BottomCenter)
                    .clickable { onUpload() }
            ) {
                Text(
                    text = "올리기",
                    modifier = Modifier.align(Alignment.Center),
                    color = SikshaColors.White900,
                    fontWeight = FontWeight.Bold
                )
            }
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
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = board.name,
                color = SikshaColors.Gray700
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
) {
}

@Composable
fun TitleEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {}
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = SikshaTypography.subtitle1,
        decorationBox = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SikshaColors.Gray100,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(
                        top = 11.dp,
                        bottom = 8.dp,
                        start = 12.dp,
                        end = 12.dp
                    ),
                contentAlignment = Alignment.TopStart
            ) {
                it()
                if (value.isEmpty()) { placeholder() }
            }
        }
    )
}

@Composable
fun ContentEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {}
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = SikshaTypography.body1,
        decorationBox = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 110.dp),
                contentAlignment = Alignment.TopStart
            ) {
                it()
                if (value.isEmpty()) { placeholder() }
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
        // TODO: 댓글 브랜치 머지 후 식샤 Checkbox로 바꾸기
        Checkbox(
            checked = isAnonymous,
            onCheckedChange = onIsAnonymousChanged,
            modifier = Modifier.size(13.dp)
        )
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
fun EditImage(
    imageUriList: List<Uri>,
    onDeleteImage: (Int) -> Unit,
    onAddImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        imageUriList.forEachIndexed { idx, uri ->
            PostImage(
                imageUri = uri,
                idx = idx,
                onDeleteImage = { onDeleteImage(idx) }
            )
        }
        AddPostImageIcon(
            modifier = Modifier.clickable { onAddImage() }
        )
    }
}

@Composable
fun PostImage(
    imageUri: Uri,
    idx: Int,
    onDeleteImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(112.dp)
            .height(113.dp)
    ) {
        Image(
            modifier = Modifier
                .size(106.dp)
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.BottomStart),
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = idx.toString() + "번째 이미지",
            contentScale = ContentScale.Crop
        )
        DeletePostImageIcon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onDeleteImage() }
        )
    }
}

@Preview
@Composable
fun CreatePostScreenPreview() {
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
            onIsAnonymousChanged = { },
            imageUriList = listOf<Uri>(Uri.parse("https://picsum.photos/200"), Uri.parse("https://picsum.photos/200"), Uri.parse("https://picsum.photos/200")),
            onDeleteImage = { },
            onAddImage = { },
            onUpload = { },
            isUploadActivated = true
        )
    }
}

@Preview
@Composable
fun CreatePostScreenPreview2() {
    SikshaTheme {
        CreatePostScreen(
            currentBoard = Board(
                name = "자유게시판"
            ),
            onNavigateUp = { },
            onOpenBoardList = { },
            titleTextValue = "",
            onTitleTextChanged = { },
            contentTextValue = "",
            onContentTextChanged = { },
            isAnonymous = true,
            onIsAnonymousChanged = { },
            imageUriList = listOf<Uri>(Uri.parse("https://picsum.photos/200"), Uri.parse("https://picsum.photos/200"), Uri.parse("https://picsum.photos/200")),
            onDeleteImage = { },
            onAddImage = { },
            onUpload = { },
            isUploadActivated = true
        )
    }
}
