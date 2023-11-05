package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.ViewModel
import com.wafflestudio.siksha2.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor() : ViewModel() {
    val dummyPosts = List(100) {
        Post(
            title = "title $it",
            content = "content $it"
        )
    }
}
