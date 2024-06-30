package com.wafflestudio.siksha2.ui.main.community

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.utils.PathUtil
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {
    private val _board = MutableStateFlow<Board>(Board())
    val board: StateFlow<Board> = _board

    private val _imageUriList = MutableStateFlow<List<Uri>>(emptyList())
    val imageUriList: StateFlow<List<Uri>>
        get() = _imageUriList

    init {
        viewModelScope.launch {
            _board.value = communityRepository.getBoard(
                CreatePostFragmentArgs.fromSavedStateHandle(savedStateHandle).boardId
            )
        }
    }

    fun addImageUri(uri: Uri) {
        val uriList = _imageUriList.value.toMutableList()
        uriList.add(uri)
        _imageUriList.value = uriList
    }

    fun createPost(context: Context, title: String, content: String, anonymous: Boolean) {
        val boardId = _board.value.id
        if (_imageUriList.value?.isNotEmpty() == true) {
            // TODO?: 로딩 state 관리
            viewModelScope.launch {
                val imageList = _imageUriList.value?.map {
                    getCompressedImage(context, it)
                }
                imageList?.let {
                    communityRepository.createPost(boardId, title, content, anonymous, imageList)
                }
            }
        } else {
            viewModelScope.launch {
                communityRepository.createPost(boardId, title, content, anonymous, emptyList())
            }
        }
    }

    private suspend fun getCompressedImage(context: Context, uri: Uri): MultipartBody.Part {
        val path = PathUtil.getPath(context, uri)
        var file = File(path)
        file = Compressor.compress(context, file) {
            resolution(300, 300)
            size(100000)
            format(Bitmap.CompressFormat.JPEG)
        }
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", file.name, requestBody)
    }
}
