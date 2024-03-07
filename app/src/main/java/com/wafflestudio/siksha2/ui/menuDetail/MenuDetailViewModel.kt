package com.wafflestudio.siksha2.ui.menuDetail

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.utils.PathUtil
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MenuDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {
    private val _menu = MutableLiveData<Menu>()
    val menu: LiveData<Menu>
        get() = _menu

    private val _commentHint = MutableLiveData<String>()
    val commentHint: LiveData<String>
        get() = _commentHint

    private val _networkResultMenuLoadingState = MutableLiveData<MenuLoadingState>()
    val networkResultMenuLoadingState: LiveData<MenuLoadingState>
        get() = _networkResultMenuLoadingState

    private val _reviewDistribution = MutableLiveData<List<Long>>()
    val reviewDistribution: LiveData<List<Long>>
        get() = _reviewDistribution

    private val _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>>
        get() = _imageUriList

    private val _leaveReviewState = MutableLiveData<LeaveReviewState>(LeaveReviewState.WAITING)
    val leaveReviewState: LiveData<LeaveReviewState>
        get() = _leaveReviewState

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviews: StateFlow<PagingData<Review>> =
        _menu.asFlow().filterNotNull().flatMapLatest { menu ->
            Pager(
                config = MenuReviewPagingSource.Config,
                pagingSourceFactory = {
                    menuRepository.menuReviewPagingSource(menu.id)
                }
            ).flow.cachedIn(viewModelScope)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = PagingData.empty())

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewsWithImage: StateFlow<PagingData<Review>> =
        _menu.asFlow().filterNotNull().flatMapLatest { menu ->
            Pager(
                config = MenuReviewWithImagePagingSource.Config,
                pagingSourceFactory = {
                    menuRepository.menuReviewWithImagePagingSource(menu.id)
                }
            ).flow.cachedIn(viewModelScope)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = PagingData.empty())

    fun refreshMenu(menuId: Long) {
        _networkResultMenuLoadingState.value = MenuLoadingState.LOADING
        viewModelScope.launch {
            try {
                _menu.value = menuRepository.getMenuById(menuId)
                _networkResultMenuLoadingState.value = MenuLoadingState.SUCCESS
            } catch (e: IOException) {
                _networkResultMenuLoadingState.value = MenuLoadingState.FAILED
            }
        }
    }

    fun getReviewsWithImages(menuId: Long): Flow<PagingData<Review>> {
        return menuRepository.getPagedReviewsOnlyHaveImagesByMenuIdFlow(menuId)
    }

    fun getRecommendationReview(score: Long) {
        // TODO: LruCache 로 캐싱해놓고 꺼내쓰기
        viewModelScope.launch {
            try {
                _commentHint.value = menuRepository.getReviewRecommendationComments(score)
            } catch (e: IOException) {
                _commentHint.value = ""
            }
        }
    }

    fun refreshReviewDistribution(menuId: Long) {
        viewModelScope.launch {
            try {
                _reviewDistribution.value = menuRepository.getReviewDistribution(menuId)
            } catch (e: IOException) {
                _reviewDistribution.value = emptyList()
            }
        }
    }

    fun addImageUri(uri: Uri, onFailure: () -> Unit) {
        val list = _imageUriList.value?.toMutableList() ?: mutableListOf()
        if (list.size < 3) {
            list.add(uri)
            _imageUriList.value = list.toList()
        } else {
            onFailure()
        }
    }

    fun deleteImageUri(index: Int, onFailure: () -> Unit = {}) {
        val list = _imageUriList.value?.toMutableList() ?: mutableListOf()
        if (index < list.size) {
            list.removeAt(index)
            _imageUriList.value = list.toList()
        } else {
            onFailure()
        }
    }

    fun refreshUriList() {
        _imageUriList.value = listOf()
    }

    fun notifySendReviewEnd() {
        _leaveReviewState.value = LeaveReviewState.WAITING
    }

    fun toggleLike() {
        if (menu.value != null) {
            _toggleLike(menu.value!!.id, menu.value!!.isLiked ?: false)
        }
    }

    private fun _toggleLike(id: Long, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            val menuItem = menuRepository.getMenuById(id)
            menuItem.isLiked = !isCurrentlyLiked
            if (menuItem.isLiked == true) {
                menuItem.likeCount = menuItem.likeCount?.plus(1)
            } else {
                menuItem.likeCount = menuItem.likeCount?.minus(1)
            }
            _menu.postValue(menuItem)
            val serverMenuItem = menuRepository.toggleLike(id, isCurrentlyLiked)
            if (serverMenuItem != menuItem) {
                _menu.postValue(serverMenuItem)
            }
        }
    }

    suspend fun leaveReview(context: Context, score: Double, comment: String) {
        val menuId = _menu.value?.id ?: return
        if (_imageUriList.value?.isNotEmpty() == true) {
            context.showToast("이미지 압축 중입니다.")
            _leaveReviewState.value = LeaveReviewState.COMPRESSING
            val imageList = _imageUriList.value?.map {
                getCompressedImage(context, it)
            }
            val commentBody = MultipartBody.Part.createFormData("comment", comment)
            imageList?.let {
                menuRepository.leaveMenuReviewImage(menuId, score.toLong(), commentBody, imageList)
            }
        } else {
            menuRepository.leaveMenuReview(menuId, score, comment)
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
