package com.wafflestudio.siksha2.ui.menu_detail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.utils.PathUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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

    private val _networkResultState = MutableLiveData<State>()
    val networkResultState: LiveData<State>
        get() = _networkResultState

    private val _reviewDistribution = MutableLiveData<List<Long>>()
    val reviewDistribution: LiveData<List<Long>>
        get() = _reviewDistribution

    private val _uriList = MutableLiveData<List<Uri>>()
    val uriList: LiveData<List<Uri>>
        get() = _uriList

    fun refreshMenu(menuId: Long) {
        _networkResultState.value = State.LOADING
        viewModelScope.launch {
            try {
                _menu.value = menuRepository.getMenuById(menuId)
                _networkResultState.value = State.SUCCESS
            } catch (e: Exception) {
                _networkResultState.value = State.FAILED
            }
        }
    }

    fun getReviews(menuId: Long): Flow<PagingData<Review>> {
        return menuRepository.getPagedReviewsByMenuIdFlow(menuId)
    }

    fun getRecommendationReview(score: Long) {
        // TODO: LruCache 로 캐싱해놓고 꺼내쓰기
        viewModelScope.launch {
            _commentHint.value = menuRepository.getReviewRecommendationComments(score)
        }
    }

    fun refreshReviewDistribution(menuId: Long) {
        viewModelScope.launch {
            _reviewDistribution.value = menuRepository.getReviewDistribution(menuId)
        }
    }

    fun addUri(uri: Uri): Boolean {
        val list = _uriList.value?.toMutableList() ?: mutableListOf()
        if (list.size >= 3) return false
        list.add(uri)
        _uriList.value = list.toList()
        return true
    }

    fun deleteUri(index: Int): Boolean {
        val list = _uriList.value?.toMutableList() ?: mutableListOf()
        if (list.size < index + 1) return false
        list.removeAt(index)
        _uriList.value = list.toList()
        return true
    }

    fun refreshUriList() {
        _uriList.value = listOf()
    }

    suspend fun leaveReview(context: Context, score: Double, comment: String) {
        _menu.value?.id?.let { id ->
            if (_uriList.value?.isNotEmpty() == true) {
                val imageList = mutableListOf<MultipartBody.Part>()
                _uriList.value?.forEach {
                    val path = PathUtil.getPath(context, it)
                    val file = File(path)
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("images", file.name, requestBody)
                    imageList.add(multipartBody)
                }
                menuRepository.leaveMenuReviewImage(id, score.toLong(), comment, imageList)
            } else {
                menuRepository.leaveMenuReview(id, score, comment)
            }
        }
    }

    enum class State {
        LOADING,
        SUCCESS,
        FAILED
    }
}
