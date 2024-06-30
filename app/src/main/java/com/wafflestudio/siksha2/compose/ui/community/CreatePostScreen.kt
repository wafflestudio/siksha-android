package com.wafflestudio.siksha2.compose.ui.community

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.verticalScroll
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
import com.wafflestudio.siksha2.utils.showToast

@Composable
fun CreatePostRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    createPostViewModel: CreatePostViewModel = hiltViewModel()
) {
    val board by createPostViewModel.board.collectAsState()
    val imageUriList by createPostViewModel.imageUriList.collectAsState()
    val createPostState by createPostViewModel.createPostState.collectAsState()

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
        onTitleTextChanged = {newInput ->
            if (titleInput.length < 200) titleInput = newInput.filter {
                (it.isLetterOrDigit() || it.isWhitespace()) && it != '\n'
            }
            else    context.showToast("제목은 200자를 넘길 수 없습니다.")
        },
        contentTextValue = contentInput,
        onContentTextChanged = {newInput ->
            if (contentInput.length < 1000) contentInput = newInput.filter {
                it.isLetterOrDigit() || it.isWhitespace()
            }
            else context.showToast("내용은 1000자를 넘길 수 없습니다.")
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
    val scrollState = rememberScrollState()
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
                    .verticalScroll(scrollState)
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
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            color = if (isUploadActivated) SikshaColors.OrangeMain else SikshaColors.Gray500,
                            shape = RoundedCornerShape(8.dp)
                        )
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
