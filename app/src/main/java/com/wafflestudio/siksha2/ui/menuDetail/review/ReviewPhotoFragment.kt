package com.wafflestudio.siksha2.ui.menuDetail.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.compose.ui.menuDetail.review.ReviewScreen
import com.wafflestudio.siksha2.databinding.FragmentReviewPhotoBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.ui.menuDetail.MenuReviewsAdapter
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReviewPhotoFragment : Fragment() {
    private lateinit var binding: FragmentReviewPhotoBinding
    private val vm: MenuDetailViewModel by activityViewModels()
    private val args: ReviewPhotoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewPhotoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.reviewPhotoComposeView.setContent {
            SikshaTheme {
                ReviewScreen(
                    navController = findNavController(),
                    showImages = true,
                    menuDetailViewModel = vm
                )
            }
        }
    }
}
