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
import com.wafflestudio.siksha2.databinding.FragmentReviewPhotoBinding
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.ui.menuDetail.MenuReviewsAdapter
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReviewPhotoFragment : Fragment() {
    private lateinit var binding: FragmentReviewPhotoBinding
    private val vm: MenuDetailViewModel by activityViewModels()
    private val args: ReviewPhotoFragmentArgs by navArgs()
    private lateinit var reviewsAdapter: MenuReviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewPhotoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reviewsAdapter = MenuReviewsAdapter(true, childFragmentManager)

        binding.reviewList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewsAdapter
        }

        lifecycleScope.launch {
            reviewsAdapter.loadStateFlow
                .collectLatest {
                    if (it.refresh is LoadState.NotLoading) {
                        (reviewsAdapter.itemCount < 1).let { empty ->
                            binding.reviewList.setVisibleOrGone(empty.not())
                            binding.textNoReviews.setVisibleOrGone(empty)
                        }
                    }
                }
        }

        lifecycleScope.launch {
            vm.getReviewsWithImages(args.menuId).collectLatest {
                reviewsAdapter.submitData(it)
            }
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
