package com.wafflestudio.siksha2.compose.ui.community

import android.content.Context
import android.net.Uri
import android.view.ViewTreeObserver
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.wafflestudio.siksha2.components.compose.Checkbox
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.ui.AddPostImageIcon
import com.wafflestudio.siksha2.ui.CancelIcon
import com.wafflestudio.siksha2.ui.DeletePostImageIcon
import com.wafflestudio.siksha2.ui.ExpandOptionsIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTypography
import com.wafflestudio.siksha2.ui.main.community.PostCreateViewModel
import com.wafflestudio.siksha2.utils.showToast

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PostCreateRoute(
    onNavigateUp: () -> Unit,
    onUploadSuccess: (Long) -> Unit,
    modifier: Modifier = Modifier,
    postCreateViewModel: PostCreateViewModel = hiltViewModel()
) {
    val board by postCreateViewModel.board.collectAsState()
    val postId by postCreateViewModel.postId.collectAsState()
    val title by postCreateViewModel.title.collectAsState()
    val content by postCreateViewModel.content.collectAsState()
    val imageUriList by postCreateViewModel.imageUriList.collectAsState()
    val createPostState by postCreateViewModel.createPostState.collectAsState()

    var isAnonymous by remember { mutableStateOf(true) }
    val keyboardState by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if (it != null) postCreateViewModel.addImageUri(it)
    }

    PostCreateScreen(
        currentBoard = board,
        onNavigateUp = onNavigateUp,
        onUploadSuccess = {
            onUploadSuccess(postId)
        },
        onOpenBoardList = { }, // TODO
        titleTextValue = title,
        onTitleTextChanged = { newInput ->
            postCreateViewModel.updateTitle(newInput, context)
        },
        contentTextValue = content,
        onContentTextChanged = { newInput ->
            postCreateViewModel.updateContent(newInput, context)
        },
        isAnonymous = isAnonymous,
        onIsAnonymousChanged = { isAnonymous = it },
        keyboardState = keyboardState,
        onCloseKeyboard = { keyboardController?.hide() },
        imageUriList = imageUriList,
        onDeleteImage = { idx ->
            postCreateViewModel.deleteImageUri(idx)
        },
        onAddImage = { launcher.launch("image/*") },
        onUpload = { postCreateViewModel.sendPost(context, isAnonymous) },
        isUploadActivated = (title.isNotEmpty()) && (content.isNotEmpty()),
        createPostState = createPostState,
        context = context,
        modifier = modifier
    )
}

@Composable
fun PostCreateScreen(
    currentBoard: Board,
    onNavigateUp: () -> Unit,
    onUploadSuccess: () -> Unit,
    onOpenBoardList: () -> Unit,
    titleTextValue: String,
    onTitleTextChanged: (String) -> Unit,
    contentTextValue: String,
    onContentTextChanged: (String) -> Unit,
    isAnonymous: Boolean,
    onIsAnonymousChanged: (Boolean) -> Unit,
    keyboardState: Boolean,
    onCloseKeyboard: () -> Unit,
    imageUriList: List<Uri>,
    onDeleteImage: (Int) -> Unit,
    onAddImage: () -> Unit,
    onUpload: () -> Unit,
    isUploadActivated: Boolean,
    createPostState: PostCreateViewModel.CreatePostState,
    context: Context,
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
                    scrollState = scrollState,
                    placeholder = {
                        Text(
                            text = "내용을 입력하세요.",
                            color = SikshaColors.Gray400
                        )
                    },
                    modifier = Modifier.weight(weight = 1.0f, fill = keyboardState)
                )
                if (keyboardState) {
                    WithKeyboardControls(
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
                    Divider(color = SikshaColors.Gray100, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    EditImage(
                        imageUriList = imageUriList,
                        onDeleteImage = onDeleteImage,
                        onAddImage = onAddImage
                    )
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
            when (createPostState) {
                PostCreateViewModel.CreatePostState.COMPRESSING, PostCreateViewModel.CreatePostState.WAITING
                -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0x88FFFFFF))
                    ) {
                    }
                }
                PostCreateViewModel.CreatePostState.SUCCESS -> {
                    context.showToast("글을 올렸습니다.")
                    onUploadSuccess()
                }
                else -> { }
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
        singleLine = true,
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
    scrollState: ScrollState,
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
            text = "익명",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isAnonymous) SikshaColors.OrangeMain else SikshaColors.Gray400
        )
    }
}

@Composable
fun WithKeyboardControls(
    modifier: Modifier = Modifier,
    isAnonymous: Boolean = true,
    onIsAnonymousChanged: (Boolean) -> Unit = {},
    onCloseKeyboard: () -> Unit
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        AnonymousCheckbox(
            isAnonymous = isAnonymous,
            onIsAnonymousChanged = onIsAnonymousChanged
        )
        Text(
            text = "OK",
            style = SikshaTypography.h1,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = SikshaColors.OrangeMain,
            modifier = Modifier.clickable { onCloseKeyboard() }
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
                color = if (isUploadActivated) SikshaColors.OrangeMain else SikshaColors.Gray500,
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
            color = SikshaColors.White900,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            keyboardState.value = ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return keyboardState
}
