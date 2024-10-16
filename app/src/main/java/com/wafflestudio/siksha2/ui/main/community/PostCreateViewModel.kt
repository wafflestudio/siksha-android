package com.wafflestudio.siksha2.ui.main.community

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.utils.ImageUtil
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class PostCreateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    // ###### 업로드할 정보 ######

    private val _board = MutableStateFlow<Board>(Board())
    val board: StateFlow<Board> = _board

    private val _title = MutableStateFlow<String>("")
    val title: StateFlow<String> = _title

    val isAnonymous = communityRepository.isAnonymous.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val _content = MutableStateFlow<String>("")
    val content: StateFlow<String> = _content

    // 올라갈 이미지의 uri들 (내부 저장소, 웹 url 공존)
    private val _imageUrisToUpload = MutableStateFlow<List<Uri>>(emptyList())
    val imageUrisToUpload: StateFlow<List<Uri>> = _imageUrisToUpload

    // ###### (편집하고 있다면) 원래 글의 정보 ######

    private val _post = MutableStateFlow<Post>(Post())
    val post: StateFlow<Post> = _post

    // 원래 있던 이미지들 (글 수정 시 다시 보내야 하므로, ByteArray 형태 저장)
    private val _downloadedImages = MutableStateFlow<Map<String, ByteArray>>(emptyMap())

    // 수정할 포스트 id
    private val _createdPostId = MutableStateFlow<Long>(-1)
    val createdPostId: StateFlow<Long> = _createdPostId

    // ###### 기타 ######

    private val _boards = MutableStateFlow<List<Board>>(listOf())
    val boards: StateFlow<List<Board>> = _boards

    private val isEdit get() = PostEditFragmentArgs.fromSavedStateHandle(savedStateHandle).postId != -1L

    private val _postCreateEvent = MutableSharedFlow<PostCreateEvent>()
    val postCreateEvent = _postCreateEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            runCatching {
                _boards.value = communityRepository.getBoards()
                val boardId: Long = PostCreateFragmentArgs.fromSavedStateHandle(savedStateHandle).boardId
                val postId: Long = PostEditFragmentArgs.fromSavedStateHandle(savedStateHandle).postId
                _postCreateEvent.emit(PostCreateEvent.FetchPostProcessing)
                // boardId와 postId 중 하나만 -1이 아니어야 하고, 나머지 경우는 오류
                if ((boardId == -1L) == (postId == -1L)) {
                    throw Exception()
                } else if (boardId != -1L) {
                    createPostInit(boardId)
                } else { // postId != -1L
                    editPostInit(postId)
                }
            }.onSuccess {
                _postCreateEvent.emit(PostCreateEvent.FetchPostSuccess)
            }.onFailure {
                _postCreateEvent.emit(PostCreateEvent.FetchPostFailed)
            }
        }
    }

    private suspend fun createPostInit(boardId: Long) {
        _board.value = communityRepository.getBoard(boardId)
    }

    private suspend fun editPostInit(postId: Long) {
        _post.value = communityRepository.getPost(postId)
        _board.value = communityRepository.getBoard(post.value.boardId)
        _title.value = post.value.title
        _content.value = post.value.content
        _imageUrisToUpload.value = post.value.etc?.images?.map { Uri.parse(it) } ?: listOf()
        _downloadedImages.value = post.value.etc?.images?.associate {
            it to downloadImageAsByteArray(it)
        }?.filterValues { it.isNotEmpty() } ?: emptyMap()
    }

    fun addImageUri(uri: Uri) {
        val uriList = _imageUrisToUpload.value.toMutableList()
        uriList.add(uri)
        _imageUrisToUpload.value = uriList
    }

    fun deleteImageUri(index: Int) {
        val uriList = _imageUrisToUpload.value.toMutableList()
        if (index < uriList.size) {
            uriList.removeAt(index)
            _imageUrisToUpload.value = uriList
        }
    }

    fun sendPost(context: Context, anonymous: Boolean) {
        if (isEdit) {
            patchPost(context, anonymous)
        } else {
            createPost(context, anonymous)
        }
    }

    private fun createPost(context: Context, anonymous: Boolean) {
        val boardId = _board.value.id
        viewModelScope.launch {
            runCatching {
                _postCreateEvent.emit(PostCreateEvent.UploadPostProcessing)
                val imageList = _imageUrisToUpload.value.map {
                    getCompressedImageAsMultipartBody(context, it)
                }
                val titleBody = MultipartBody.Part.createFormData("title", title.value)
                val contentBody = MultipartBody.Part.createFormData("content", content.value)
                var response: Post?
                imageList.let {
                    response = communityRepository.createPost(boardId, titleBody, contentBody, anonymous, imageList)
                }
                _createdPostId.value = response?.id ?: -1
            }.onSuccess {
                _postCreateEvent.emit(PostCreateEvent.UploadPostSuccess)
            }.onFailure {
                _postCreateEvent.emit(PostCreateEvent.UploadPostFailed)
            }
        }
    }

    private fun patchPost(context: Context, anonymous: Boolean) {
        val boardId = _board.value.id
        viewModelScope.launch {
            runCatching {
                _postCreateEvent.emit(PostCreateEvent.UploadPostProcessing)
                val imageList = _imageUrisToUpload.value.map { uri ->
                    if (_downloadedImages.value.containsKey(uri.toString())) {
                        val filename = uri.toString()
                        byteArrayToMultipartBody(filename, _downloadedImages.value.getValue(filename))
                    } else {
                        getCompressedImageAsMultipartBody(context, uri)
                    }
                }
                val titleBody = MultipartBody.Part.createFormData("title", title.value)
                val contentBody = MultipartBody.Part.createFormData("content", content.value)
                val response = imageList.let {
                    communityRepository.patchPost(_post.value.id, boardId, titleBody, contentBody, anonymous, imageList)
                }
                _createdPostId.value = response.id
            }.onSuccess {
                _postCreateEvent.emit(PostCreateEvent.UploadPostSuccess)
            }.onFailure {
                _postCreateEvent.emit(PostCreateEvent.UploadPostFailed)
            }
        }
    }

    private fun byteArrayToMultipartBody(filename: String, byteArray: ByteArray): MultipartBody.Part {
        val requestBody = byteArray
            .toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", filename, requestBody)
    }

    private suspend fun getCompressedImageAsMultipartBody(context: Context, uri: Uri): MultipartBody.Part {
        val file = ImageUtil.getCompressedImage(context, uri)
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", file.name, requestBody)
    }

    fun selectBoard(newBoard: Board) {
        _board.value = newBoard
    }

    fun updateTitle(newTitle: String, context: Context) {
        if (newTitle.length < 200) {
            _title.value = newTitle.filter {
                it != '\n'
            }
        } else {
            context.showToast("제목은 200자를 넘길 수 없습니다.")
        }
    }

    fun updateContent(newContent: String, context: Context) {
        if (newContent.length < 1000) {
            _content.value = newContent
        } else {
            context.showToast("내용은 1000자를 넘길 수 없습니다.")
        }
    }

    fun setIsAnonymous(isAnonymous: Boolean) {
        communityRepository.setIsAnonymous(isAnonymous)
    }

    private suspend fun downloadImageAsByteArray(imageUrl: String): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                url.readBytes()
            } catch (e: Exception) {
                ByteArray(0)
            }
        }
    }
}

sealed interface PostCreateEvent {
    object UploadPostSuccess : PostCreateEvent
    object UploadPostFailed : PostCreateEvent
    object UploadPostProcessing : PostCreateEvent
    object FetchPostSuccess : PostCreateEvent
    object FetchPostFailed : PostCreateEvent
    object FetchPostProcessing : PostCreateEvent
}
