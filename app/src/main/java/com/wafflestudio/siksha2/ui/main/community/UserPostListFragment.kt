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
import com.wafflestudio.siksha2.compose.ui.community.UserPostListRoute
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPostListFragment : Fragment() {
    private val userPostListViewModel: UserPostListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_post_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ComposeView>(R.id.community_compose_view).setContent {
            SikshaTheme {
                UserPostListRoute(
                    modifier = Modifier.fillMaxSize(),
                    userPostListViewModel = userPostListViewModel,
                    onClickPost = { postId ->
                        findNavController().navigate(
                            UserPostListFragmentDirections.actionUserPostListFragmentToPostDetailFragment(
                                postId
                            )
                        )
                    },
                    onNavigateUp = { findNavController().navigateUp() }
                )
            }
        }
    }
}
