package com.wafflestudio.siksha2.ui.menu_detail

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
import com.wafflestudio.siksha2.databinding.FragmentMenuDetailBinding
import com.wafflestudio.siksha2.utils.dp
import com.wafflestudio.siksha2.utils.showToast
import com.wafflestudio.siksha2.utils.visibleOrGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.round

@AndroidEntryPoint
class MenuDetailFragment : Fragment() {
    private val vm: MenuDetailViewModel by activityViewModels()

    private lateinit var binding: FragmentMenuDetailBinding
    private val args: MenuDetailFragmentArgs by navArgs()
    private val reviewsAdapter: MenuReviewsAdapter = MenuReviewsAdapter(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuDetailBinding.inflate(inflater, container, false)
        binding.reviewList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewsAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            reviewsAdapter.loadStateFlow
                .collectLatest {
                    if (it.refresh is LoadState.NotLoading) {
                        (reviewsAdapter.itemCount < 1).let { empty ->
                            binding.emptyList.visibleOrGone(empty)
                            binding.reviewList.visibleOrGone(empty.not())
                        }
                    }
                }
        }

        vm.refreshMenu(args.menuId)
        vm.refreshImages(args.menuId)
        vm.refreshReviewDistribution(args.menuId)

        vm.networkResultState.observe(viewLifecycleOwner) {
            binding.menuInfoContainer.visibleOrGone(it == MenuDetailViewModel.State.SUCCESS)
            binding.onErrorContainer.root.visibleOrGone(it == MenuDetailViewModel.State.FAILED)
            binding.onLoadingContainer.root.visibleOrGone(it == MenuDetailViewModel.State.LOADING)
        }

        vm.menu.observe(viewLifecycleOwner) { menu ->
            // for marquee
            binding.menuTitle.isSelected = true
            binding.menuTitle.text = menu?.nameKr
            binding.menuRating.text = "${ menu?.score?.times(10)?.let { round(it) / 10 } ?: "0.0"}"
            binding.menuStars.rating = menu?.score?.toFloat() ?: 0.0f
            binding.reviewCount.text = "${menu?.reviewCount ?: 0}명"
        }

        vm.reviewDistribution.observe(viewLifecycleOwner) { distList ->
            if (distList.isEmpty()) return@observe
            val distBarList = listOf(binding.distBar1, binding.distBar2, binding.distBar3, binding.distBar4, binding.distBar5)
            var maxCount = 1L
            distList.forEach { if (maxCount < it) maxCount = it }
            distBarList.forEachIndexed { index, bar ->
                val params = bar.layoutParams
                val ratio = distList[index].toDouble() / maxCount.toDouble()
                Timber.d("idx = $index ratio = $ratio")
                if (ratio != 0.0) params.width = (requireContext().dp(MAX_REVIEW_DIST_BAR_WIDTH_DP) * ratio).toInt()
                else params.width = requireContext().dp(NO_REVIEW_DIST_BAR_WIDTH_DP)
                bar.layoutParams = params
                bar.requestLayout()
            }
        }

        vm.imageCount.observe(viewLifecycleOwner) { imageCount ->
            binding.layoutPhotoReview.visibleOrGone(imageCount > 0)
            if (imageCount > 3) {
                binding.reviewImageView3.showMorePhotos(imageCount - 3)
                binding.reviewImageView3.setOnClickListener {
                    val action = MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewPhotoFragment(args.menuId)
                    findNavController().navigate(action)
                }
            }
        }

        vm.imageUrlList.observe(viewLifecycleOwner) { imageUrlList ->
            val imageReviewList = listOf(binding.reviewImageView1, binding.reviewImageView2, binding.reviewImageView3)
            for (i in 0 until 3) {
                if (i < imageUrlList.size) {
                    imageReviewList[i].run {
                        setImage(imageUrlList[i])
                        visibleOrGone(true)
                    }
                }
            }
        }

        lifecycleScope.launch {
            vm.getReviews(args.menuId).collectLatest {
                reviewsAdapter.submitData(it)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutCollectPhotoReviews.setOnClickListener {
            val action = MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewPhotoFragment(args.menuId)
            findNavController().navigate(action)
        }

        binding.layoutCollectReviews.setOnClickListener {
            val action = MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewFragment(args.menuId)
            findNavController().navigate(action)
        }

        binding.leaveReviewButton.setOnClickListener {
            if (args.isTodayMenu) {
                val action =
                    MenuDetailFragmentDirections.actionMenuDetailFragmentToLeaveReviewFragment()
                findNavController().navigate(action)
            } else {
                showToast("오늘 메뉴만 평가할 수 있습니다.")
            }
        }
    }

    companion object {
        private const val NO_REVIEW_DIST_BAR_WIDTH_DP = 8
        private const val MAX_REVIEW_DIST_BAR_WIDTH_DP = 180
    }
}
