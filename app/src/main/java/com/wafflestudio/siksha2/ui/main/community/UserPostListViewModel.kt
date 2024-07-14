package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.ViewModel
import com.wafflestudio.siksha2.repositories.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserPostListViewModel@Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {
}
