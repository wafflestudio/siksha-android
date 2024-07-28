package com.wafflestudio.siksha2.ui.main.community

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.utils.PathUtil
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostCreateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    private val _board = MutableStateFlow<Board>(Board())
    val board: StateFlow<Board> = _board

    private val _post = MutableStateFlow<Post>(Post())
    val post: StateFlow<Post> = _post

    private val _imageUriList = MutableStateFlow<List<Uri>>(emptyList())
    val imageUriList: StateFlow<List<Uri>> = _imageUriList

    private val _createPostState = MutableStateFlow(CreatePostState.USER_INPUT)
    val createPostState: StateFlow<CreatePostState> = _createPostState

    private val _postId = MutableStateFlow<Long>(-1)
    val postId: StateFlow<Long> = _postId

    private var isEdit = false

    init {
        viewModelScope.launch {
            val boardId: Long = PostCreateFragmentArgs.fromSavedStateHandle(savedStateHandle).boardId
            val postId: Long = PostEditFragmentArgs.fromSavedStateHandle(savedStateHandle).postId
            if (boardId !=  -1L){
                _board.value = communityRepository.getBoard(boardId)
                isEdit = false
            }
            else if (postId != -1L){
                _post.value = communityRepository.getPost(postId)
                _board.value = communityRepository.getBoard(post.value.boardId)
                isEdit = true
            }
            else {
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

    fun sendPost(context: Context, title: String, content: String, anonymous: Boolean) {
        if (isEdit)
            patchPost(context, title, content, anonymous)
        else
            createPost(context, title, content, anonymous)
    }

    fun createPost(context: Context, title: String, content: String, anonymous: Boolean) {
        val boardId = _board.value.id
        viewModelScope.launch {
            try {
                _createPostState.value = CreatePostState.COMPRESSING
                val imageList = _imageUriList.value.map {
                    getCompressedImage(context, it)
                }
                val titleBody = MultipartBody.Part.createFormData("title", title)
                val contentBody = MultipartBody.Part.createFormData("content", content)
                _createPostState.value = CreatePostState.WAITING
                var response: Post?
                imageList.let {
                    response = communityRepository.createPost(boardId, titleBody, contentBody, anonymous, imageList)
                }
                _postId.value = response?.id ?: -1
                _createPostState.value = CreatePostState.SUCCESS
            } catch (e: Exception) {
                throw e
                Timber.tag("CreatePostViewModel.createPost").d(e.message)
                // Timber.tag("CreatePostViewModel.createPost").d("asdf")
                context.showToast("오류가 발생했습니다. 다시 시도해 주세요.")
                _createPostState.value = CreatePostState.USER_INPUT
            }
        }
    }

    fun patchPost(context: Context, title: String, content: String, anonymous: Boolean) {
        val boardId = _board.value.id
        viewModelScope.launch {
            try {
                _createPostState.value = CreatePostState.COMPRESSING
                val imageList = _imageUriList.value.map {
                    getCompressedImage(context, it)
                }
                val titleBody = MultipartBody.Part.createFormData("title", title)
                val contentBody = MultipartBody.Part.createFormData("content", content)
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

    private suspend fun getCompressedImage(context: Context, uri: Uri): MultipartBody.Part {
        val path = PathUtil.getPath(context, uri)
//        if (path == null) {
//            Timber.tag("CreatePostViewModel.createPost").d("path null")
//        }
        var file = File(path)
        file = Compressor.compress(context, file) {
            resolution(300, 300)
            size(100000)
            format(Bitmap.CompressFormat.JPEG)
        }
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", file.name, requestBody)
    }

    enum class CreatePostState {
        USER_INPUT,
        COMPRESSING,
        WAITING,
        SUCCESS
    }
}
