package com.wafflestudio.siksha2.compose.ui.community

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.Checkbox
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.ui.AddPostImageIcon
import com.wafflestudio.siksha2.ui.CancelIcon
import com.wafflestudio.siksha2.ui.CheckSimpleIcon
import com.wafflestudio.siksha2.ui.DeletePostImageIcon
import com.wafflestudio.siksha2.ui.ExpandOptionsIcon
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.PostCreateEvent
import com.wafflestudio.siksha2.ui.main.community.PostCreateViewModel
import com.wafflestudio.siksha2.utils.KeyboardUtil.keyboardAsState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PostCreateRoute(
    onNavigateUp: () -> Unit,
    onUploadSuccess: (Long) -> Unit,
    modifier: Modifier = Modifier,
    postCreateViewModel: PostCreateViewModel = hiltViewModel()
) {
    val boards by postCreateViewModel.boards.collectAsState()
    val board by postCreateViewModel.board.collectAsState()
    val postId by postCreateViewModel.createdPostId.collectAsState()
    val title by postCreateViewModel.title.collectAsState()
    val content by postCreateViewModel.content.collectAsState()
    val imageUriList by postCreateViewModel.imageUrisToUpload.collectAsState()
    val isAnonymous by postCreateViewModel.isAnonymous.collectAsState()

    val keyboardState by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var isBoardListOpen by remember { mutableStateOf(false) }
    var screenClickEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if (it != null) postCreateViewModel.addImageUri(it)
    }

    PostCreateScreen(
        boardsList = boards,
        currentBoard = board,
        onNavigateUp = onNavigateUp,
        onUploadSuccess = {
            onUploadSuccess(postId)
        },
        isBoardListOpen = isBoardListOpen,
        onCloseBoardList = { isBoardListOpen = false },
        onToggleBoardList = { isBoardListOpen = !isBoardListOpen },
        onSelectBoard = {
            postCreateViewModel.selectBoard(it)
            isBoardListOpen = false
        },
        titleTextValue = title,
        onTitleTextChanged = { newInput ->
            postCreateViewModel.updateTitle(newInput, context)
        },
        contentTextValue = content,
        onContentTextChanged = { newInput ->
            postCreateViewModel.updateContent(newInput, context)
        },
        isAnonymous = isAnonymous,
        onIsAnonymousChanged = { postCreateViewModel.setIsAnonymous(it) },
        isKeyboardOpen = keyboardState,
        onCloseKeyboard = { keyboardController?.hide() },
        imageUriList = imageUriList,
        onDeleteImage = { idx ->
            postCreateViewModel.deleteImageUri(idx)
        },
        onAddImage = { launcher.launch("image/*") },
        onUpload = { postCreateViewModel.sendPost(context, isAnonymous) },
        isUploadActivated = (title.isNotEmpty()) && (content.isNotEmpty()),
        screenClickEnabled = screenClickEnabled,
        onEnableScreenClick = { screenClickEnabled = true },
        onDisableScreenClick = { screenClickEnabled = false },
        postCreateEvent = postCreateViewModel.postCreateEvent,
        modifier = modifier
    )
}

@Composable
fun PostCreateScreen(
    boardsList: List<Board>,
    currentBoard: Board,
    onNavigateUp: () -> Unit,
    onUploadSuccess: () -> Unit,
    isBoardListOpen: Boolean,
    onCloseBoardList: () -> Unit,
    onToggleBoardList: () -> Unit,
    onSelectBoard: (Board) -> Unit,
    titleTextValue: String,
    onTitleTextChanged: (String) -> Unit,
    contentTextValue: String,
    onContentTextChanged: (String) -> Unit,
    isAnonymous: Boolean,
    onIsAnonymousChanged: (Boolean) -> Unit,
    isKeyboardOpen: Boolean,
    onCloseKeyboard: () -> Unit,
    imageUriList: List<Uri>,
    onDeleteImage: (Int) -> Unit,
    onAddImage: () -> Unit,
    onUpload: () -> Unit,
    isUploadActivated: Boolean,
    screenClickEnabled: Boolean,
    onEnableScreenClick: () -> Unit,
    onDisableScreenClick: () -> Unit,
    postCreateEvent: SharedFlow<PostCreateEvent>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val closeBoardSelectorInteractionSource = remember { MutableInteractionSource() }
    val blockClickWhileLoadingInteractionSource = remember { MutableInteractionSource() }

    PostCreateViewEventEffect(
        postCreateEvent = postCreateEvent,
        enableScreenClick = onEnableScreenClick,
        disableScreenClick = onDisableScreenClick,
        onUploadSuccess = onUploadSuccess,
        onFetchFailed = onNavigateUp
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SikshaTheme.colors.BackgroundPrimary)
    ) {
        TopBar(
            title = stringResource(R.string.community_create_screen_title),
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
                    onClick = onToggleBoardList
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        TitleEditText(
                            value = titleTextValue,
                            onValueChange = onTitleTextChanged,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.community_create_title_placeholder),
                                    color = SikshaTheme.colors.Gray500,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        ContentEditText(
                            value = contentTextValue,
                            onValueChange = onContentTextChanged,
                            scrollState = scrollState,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.community_create_content_placeholder),
                                    color = SikshaTheme.colors.Gray300,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.weight(weight = 1.0f, fill = isKeyboardOpen)
                        )
                        if (isKeyboardOpen) {
                            KeyboardToolbar(
                                isAnonymous = isAnonymous,
                                onIsAnonymousChanged = onIsAnonymousChanged,
                                onCloseKeyboard = onCloseKeyboard
                            )
                        } else {
                            Spacer(modifier = Modifier.height(13.dp))
                            AnonymousCheckbox(
                                isAnonymous = isAnonymous,
                                onIsAnonymousChanged = onIsAnonymousChanged,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = SikshaTheme.colors.Gray100, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(13.dp))
                            PostImages(
                                imageUriList = imageUriList,
                                onDeleteImage = onDeleteImage,
                                onAddImage = onAddImage
                            )
                        }
                    }
                    if (isBoardListOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = closeBoardSelectorInteractionSource,
                                    indication = null
                                ) { onCloseBoardList() }
                        ) { }
                        BoardSelector(
                            boards = boardsList,
                            currentBoard = currentBoard,
                            onSelectBoard = onSelectBoard,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
                if (!isKeyboardOpen) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        UploadButton(
                            isUploadActivated = isUploadActivated,
                            onUpload = onUpload
                        )
                    }
                }
            }
            if (!screenClickEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = SikshaTheme.colors.White)
                        .alpha(0.8f)
                        .clickable(
                            interactionSource = blockClickWhileLoadingInteractionSource,
                            indication = null
                        ) { }
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
            .background(
                color = SikshaTheme.colors.BackgroundSecondary,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                color = SikshaTheme.colors.Gray200,
                width = 1.dp,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable {
                onClick()
            }
            .padding(top = 11.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = board.name,
                color = SikshaTheme.colors.Gray700,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExpandOptionsIcon(
                color = SikshaTheme.colors.Gray600
            )
        }
    }
}

@Composable
fun BoardSelectorCard(
    board: Board,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSelected) SikshaTheme.colors.Orange500 else SikshaTheme.colors.Gray700
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(35.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = board.name,
                color = textColor,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (isSelected) {
                CheckSimpleIcon()
            } else {
                Spacer(modifier = Modifier.size(9.dp))
            }
        }
    }
}

@Composable
fun BoardSelector(
    boards: List<Board>,
    currentBoard: Board,
    onSelectBoard: (Board) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = SikshaTheme.colors.BackgroundSecondary)
            .border(
                width = 1.dp,
                color = SikshaTheme.colors.Gray200,
                shape = RoundedCornerShape(8.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        boards.forEachIndexed { idx, board ->
            BoardSelectorCard(
                board = board,
                isSelected = (board == currentBoard),
                onClick = { onSelectBoard(board) }
            )
            if (idx != boards.size - 1) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = SikshaTheme.colors.BorderPrimary
                )
            }
        }
    }
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
        singleLine = true,
        textStyle = SikshaTypography.subtitle1.copy(color = SikshaTheme.colors.Black, fontSize = 14.sp),
        decorationBox = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SikshaTheme.colors.Gray50,
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
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {}
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = SikshaTypography.body1.copy(color = SikshaTheme.colors.Black, fontSize = 14.sp),
        cursorBrush = SolidColor(SikshaTheme.colors.Orange500),
        decorationBox = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 132.dp)
                    .verticalScroll(scrollState),
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
        Checkbox(
            checked = isAnonymous,
            onCheckedChange = onIsAnonymousChanged,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = stringResource(R.string.community_create_anonymous),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isAnonymous) SikshaTheme.colors.Orange500 else SikshaTheme.colors.Gray600
        )
    }
}

@Composable
fun KeyboardToolbar(
    isAnonymous: Boolean,
    onIsAnonymousChanged: (Boolean) -> Unit,
    onCloseKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AnonymousCheckbox(
            isAnonymous = isAnonymous,
            onIsAnonymousChanged = onIsAnonymousChanged
        )
        Text(
            text = stringResource(R.string.community_create_submit_content),
            style = SikshaTypography.h1,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = SikshaTheme.colors.Orange500,
            modifier = Modifier.clickable { onCloseKeyboard() }
        )
    }
}

@Composable
fun PostImages(
    imageUriList: List<Uri>,
    onDeleteImage: (Int) -> Unit,
    onAddImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        imageUriList.forEachIndexed { idx, uri ->
            PostImage(
                imageUri = uri,
                contentDescription = idx.toString() + stringResource(R.string.community_create_image_idx),
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
    contentDescription: String,
    onDeleteImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .size(106.dp)
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.BottomStart),
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop
        )
        DeletePostImageIcon(
            modifier = Modifier
                .offset(x = 7.dp, y = (-6).dp)
                .align(Alignment.TopEnd)
                .clickable { onDeleteImage() }
        )
    }
}

@Composable
fun UploadButton(
    isUploadActivated: Boolean,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .padding(bottom = 30.dp)
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = if (isUploadActivated) SikshaTheme.colors.Orange500 else SikshaTheme.colors.Gray600,
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (isUploadActivated) {
                    Modifier.clickable { onUpload() }
                } else {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { }
                }
            )
    ) {
        Text(
            text = "올리기",
            modifier = Modifier.align(Alignment.Center),
            color = SikshaTheme.colors.TextButton,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun PostCreateViewEventEffect(
    postCreateEvent: SharedFlow<PostCreateEvent>,
    enableScreenClick: () -> Unit,
    disableScreenClick: () -> Unit,
    onUploadSuccess: () -> Unit,
    onFetchFailed: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        postCreateEvent.collect {
            when (it) {
                is PostCreateEvent.UploadPostSuccess -> {
                    Toast.makeText(context, context.getString(R.string.community_create_success), Toast.LENGTH_SHORT).show()
                    onUploadSuccess()
                }
                is PostCreateEvent.UploadPostFailed -> {
                    Toast.makeText(context, "업로드 중 오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    enableScreenClick()
                }
                is PostCreateEvent.FetchPostSuccess -> {
                    enableScreenClick()
                }
                is PostCreateEvent.FetchPostFailed -> {
                    Toast.makeText(context, "서버에서 데이터를 가져오는 중 오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    onFetchFailed()
                }
                is PostCreateEvent.UploadPostProcessing,
                is PostCreateEvent.FetchPostProcessing -> {
                    disableScreenClick()
                }
            }
        }
    }
}

@Preview
@Composable
fun BoardSelectorPreview() {
    SikshaTheme {
        BoardSelector(
            boards = listOf(Board(name = "학식"), Board(name = "외식"), Board(name = "간식")),
            currentBoard = Board(name = "학식"),
            onSelectBoard = {}
        )
    }
}

@Preview
@Composable
fun PostImagesPreview() {
    SikshaTheme {
        PostImages(
            imageUriList = listOf(Uri.parse("picsum.photos/200/300"), Uri.parse("picsum.photos/200/300")),
            onDeleteImage = {},
            onAddImage = {}
        )
    }
}

@Preview
@Composable
fun PostImagePreview() {
    SikshaTheme {
        PostImage(
            imageUri = Uri.parse("picsum.photos/200/300"),
            contentDescription = "",
            onDeleteImage = {}
        )
    }
}

@Preview
@Composable
fun ActivatedUploadButtonPreview() {
    SikshaTheme {
        UploadButton(isUploadActivated = true, onUpload = { })
    }
}

@Preview
@Composable
fun DeactivatedUploadButtonPreview() {
    SikshaTheme {
        UploadButton(isUploadActivated = false, onUpload = { })
    }
}

@Preview(device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun PostCreateScreenPreview() {
    SikshaTheme {
        PostCreateScreen(
            boardsList = listOf(Board(name = "학식"), Board(name = "외식"), Board(name = "간식")),
            currentBoard = Board(name = "학식"),
            onNavigateUp = {},
            onUploadSuccess = {},
            isBoardListOpen = true,
            onCloseBoardList = {},
            onToggleBoardList = {},
            onSelectBoard = {},
            titleTextValue = "",
            onTitleTextChanged = {},
            contentTextValue = "",
            onContentTextChanged = {},
            isAnonymous = true,
            onIsAnonymousChanged = {},
            isKeyboardOpen = false,
            onCloseKeyboard = {},
            imageUriList = listOf(Uri.parse("picsum.photos/200/300"), Uri.parse("picsum.photos/200/300")),
            onDeleteImage = {},
            onAddImage = {},
            onUpload = {},
            isUploadActivated = false,
            screenClickEnabled = true,
            onEnableScreenClick = {},
            onDisableScreenClick = {},
            postCreateEvent = MutableSharedFlow()
        )
    }
}

@Preview(device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun PostCreateScreenPreview2() {
    SikshaTheme {
        PostCreateScreen(
            boardsList = listOf(Board(name = "학식"), Board(name = "외식"), Board(name = "간식")),
            currentBoard = Board(name = "학식"),
            onNavigateUp = {},
            onUploadSuccess = {},
            isBoardListOpen = false,
            onCloseBoardList = {},
            onToggleBoardList = {},
            onSelectBoard = {},
            titleTextValue = "제목제목",
            onTitleTextChanged = {},
            contentTextValue = "내용내용\nㅇ\nㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ\n" +
                "ㅇ",
            onContentTextChanged = {},
            isAnonymous = true,
            onIsAnonymousChanged = {},
            isKeyboardOpen = true,
            onCloseKeyboard = {},
            imageUriList = listOf(Uri.parse("picsum.photos/200/300"), Uri.parse("picsum.photos/200/300")),
            onDeleteImage = {},
            onAddImage = {},
            onUpload = {},
            isUploadActivated = false,
            screenClickEnabled = true,
            onEnableScreenClick = {},
            onDisableScreenClick = {},
            postCreateEvent = MutableSharedFlow()
        )
    }
}
