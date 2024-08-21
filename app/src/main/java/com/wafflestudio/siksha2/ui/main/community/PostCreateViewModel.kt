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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    private val _boards = MutableStateFlow<List<Board>>(listOf())
    val boards: StateFlow<List<Board>> = _boards

    private val _board = MutableStateFlow<Board>(Board())
    val board: StateFlow<Board> = _board

    private val _post = MutableStateFlow<Post>(Post())
    val post: StateFlow<Post> = _post

    private val _title = MutableStateFlow<String>("")
    val title: StateFlow<String> = _title

    private val _content = MutableStateFlow<String>("")
    val content: StateFlow<String> = _content

    private val _imageUriList = MutableStateFlow<List<Uri>>(emptyList())
    val imageUriList: StateFlow<List<Uri>> = _imageUriList

    private val _imageFileList = MutableStateFlow<Map<String, ByteArray>>(emptyMap())
    val imageFileList: StateFlow<Map<String, ByteArray>> = _imageFileList

    private val _createPostState = MutableStateFlow(CreatePostState.USER_INPUT)
    val createPostState: StateFlow<CreatePostState> = _createPostState

    private val _postId = MutableStateFlow<Long>(-1)
    val postId: StateFlow<Long> = _postId

    private var isEdit = false

    init {
        viewModelScope.launch {
            _boards.value = communityRepository.getBoards()
            val boardId: Long = PostCreateFragmentArgs.fromSavedStateHandle(savedStateHandle).boardId
            val postId: Long = PostEditFragmentArgs.fromSavedStateHandle(savedStateHandle).postId
            if (boardId != -1L) {
                _board.value = communityRepository.getBoard(boardId)
                isEdit = false
            } else if (postId != -1L) {
                _post.value = communityRepository.getPost(postId)
                _board.value = communityRepository.getBoard(post.value.boardId)
                _title.value = post.value.title
                _content.value = post.value.content
                _imageUriList.value = post.value.etc?.images?.map { Uri.parse(it) } ?: listOf()
                _imageFileList.value = post.value.etc?.images?.associate {
                    it to downloadFile(it)
                }?.filterValues { it.isNotEmpty() } ?: emptyMap()
                isEdit = true
            } else {
                // error handling
            }
            _createPostState.value = CreatePostState.USER_INPUT
        }
    }

    fun addImageUri(uri: Uri) {
        val uriList = _imageUriList.value.toMutableList()
        uriList.add(uri)
        _imageUriList.value = uriList
    }

    fun deleteImageUri(index: Int) {
        val uriList = _imageUriList.value.toMutableList()
        if (index < uriList.size) {
            uriList.removeAt(index)
            _imageUriList.value = uriList
        }
    }

    fun sendPost(context: Context, anonymous: Boolean) {
        if (isEdit) {
            patchPost(context, anonymous)
        } else {
            createPost(context, anonymous)
        }
    }

    fun createPost(context: Context, anonymous: Boolean) {
        val boardId = _board.value.id
        viewModelScope.launch {
            try {
                _createPostState.value = CreatePostState.COMPRESSING
                val imageList = _imageUriList.value.map {
                    getCompressedImageAsMultipartBody(context, it)
                }
                val titleBody = MultipartBody.Part.createFormData("title", title.value)
                val contentBody = MultipartBody.Part.createFormData("content", content.value)
                _createPostState.value = CreatePostState.WAITING
                var response: Post?
                imageList.let {
                    response = communityRepository.createPost(boardId, titleBody, contentBody, anonymous, imageList)
                }
                _postId.value = response?.id ?: -1
                _createPostState.value = CreatePostState.SUCCESS
            } catch (e: Exception) {
                context.showToast("오류가 발생했습니다. 다시 시도해 주세요.")
                _createPostState.value = CreatePostState.USER_INPUT
            }
        }
    }

    fun patchPost(context: Context, anonymous: Boolean) {
        val boardId = _board.value.id
        viewModelScope.launch {
            try {
                _createPostState.value = CreatePostState.COMPRESSING
                val imageList = _imageUriList.value.map { uri ->
                    if (_imageFileList.value.containsKey(uri.toString())) {
                        val filename = uri.toString()
                        byteArrayToMultipartBody(filename, _imageFileList.value.getValue(filename))
                    } else {
                        getCompressedImageAsMultipartBody(context, uri)
                    }
                }

                val titleBody = MultipartBody.Part.createFormData("title", title.value)
                val contentBody = MultipartBody.Part.createFormData("content", content.value)
                _createPostState.value = CreatePostState.WAITING
                var response: Post?
                imageList.let {
                    response = communityRepository.patchPost(_post.value.id, boardId, titleBody, contentBody, anonymous, imageList)
                }
                _postId.value = response?.id ?: -1
                _createPostState.value = CreatePostState.SUCCESS
            } catch (e: Exception) {
                throw e
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

    private suspend fun downloadFile(str: String): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(str)
                url.readBytes()
            } catch (e: Exception) {
                ByteArray(0)
            }
        }
    }

    enum class CreatePostState {
        USER_INPUT,
        COMPRESSING,
        WAITING,
        SUCCESS
    }
}
