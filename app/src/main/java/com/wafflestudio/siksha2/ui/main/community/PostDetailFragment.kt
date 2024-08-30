package com.wafflestudio.siksha2.ui.main.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.community.PostDetailRoute
import com.wafflestudio.siksha2.ui.SikshaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private val postListViewModel: PostListViewModel by activityViewModels()
    private val userPostListViewModel: UserPostListViewModel by activityViewModels() // FIXME: UserPostListViewModel을 acitivityViewModel로 유지할 필요 없다. 수정 필요

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ComposeView>(R.id.community_compose_view).setContent {
            SikshaTheme {
                PostDetailRoute(
                    onNavigateUp = { findNavController().navigateUp() },
                    onNavigateToPostReport = { postId ->
                        val action =
                            PostDetailFragmentDirections.actionPostDetailFragmentToPostReportFragment(
                                postId
                            )
                        findNavController().navigate(action)
                    },
                    onNavigateToCommentReport = { commentId ->
                        val action =
                            PostDetailFragmentDirections.actionPostDetailFragmentToCommentReportFragment(
                                commentId
                            )
                        findNavController().navigate(action)
                    },
                    modifier = Modifier.fillMaxSize(),
                    postListViewModel = postListViewModel,
                    userPostListViewModel = userPostListViewModel
                )
            }
        }
    }
}
