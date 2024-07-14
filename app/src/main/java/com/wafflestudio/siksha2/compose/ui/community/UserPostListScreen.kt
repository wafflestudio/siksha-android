package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.siksha2.ui.main.community.UserPostListViewModel

@Composable
fun UserPostListRoute(
    onClickPost: (Long) -> Unit,
    modifier: Modifier = Modifier,
    postListViewModel: UserPostListViewModel = hiltViewModel()
){
    UserPostListScreen()
}

@Composable
fun UserPostListScreen(

) {
}
