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
import com.wafflestudio.siksha2.compose.ui.community.PostListRoute
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostListFragment : Fragment() {

    private val postListViewModel: PostListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ComposeView>(R.id.community_compose_view).setContent {
            SikshaTheme {
                PostListRoute(
                    modifier = Modifier.fillMaxSize(),
                    postListViewModel = postListViewModel,
                    onClickPost = { postId ->
                        findNavController().navigate(
                            MainFragmentDirections.actionMainFragmentToPostDetailFragment(
                                postId
                            )
                        )
                    },
                    onNewPost = { id ->
                        findNavController().navigate(
                            MainFragmentDirections.actionMainFragmentToCreatePostFragment()
                                .apply { boardId = id }
                        )
                    }
                )
            }
        }
    }
}
