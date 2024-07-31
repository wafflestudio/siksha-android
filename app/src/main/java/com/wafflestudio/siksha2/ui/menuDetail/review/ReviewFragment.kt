package com.wafflestudio.siksha2.ui.menuDetail.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.compose.ui.menuDetail.review.ReviewRoute
import com.wafflestudio.siksha2.databinding.FragmentReviewBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel

class ReviewFragment : Fragment() {

    private lateinit var binding: FragmentReviewBinding
    private val vm: MenuDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.reviewComposeView.setContent {
            SikshaTheme {
                ReviewRoute(
                    showImages = true,
                    onNavigateUp = { findNavController().popBackStack() },
                    menuDetailViewModel = vm
                )
            }
        }
    }
}
