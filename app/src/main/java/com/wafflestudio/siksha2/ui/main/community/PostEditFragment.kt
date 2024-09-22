package com.wafflestudio.siksha2.ui.main.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.community.PostCreateRoute
import com.wafflestudio.siksha2.ui.SikshaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostEditFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ComposeView>(R.id.community_compose_view).setContent {
            SikshaTheme {
                PostCreateRoute(
                    onNavigateUp = {
                        findNavController().navigateUp()
                    },
                    onUploadSuccess = { postId ->
                        findNavController().navigate(
                            PostEditFragmentDirections.actionPostEditFragmentToPostDetailFragment(
                                postId
                            )
                        )
                    }
                )
            }
        }
    }
}
