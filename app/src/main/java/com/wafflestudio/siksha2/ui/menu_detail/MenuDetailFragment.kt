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
import com.wafflestudio.siksha2.utils.showToast
import com.wafflestudio.siksha2.utils.visibleOrGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuDetailFragment : Fragment() {
    private val vm: MenuDetailViewModel by activityViewModels()

    private lateinit var binding: FragmentMenuDetailBinding
    private val args: MenuDetailFragmentArgs by navArgs()
    private val reviewsAdapter: MenuReviewsAdapter = MenuReviewsAdapter()

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

        vm.networkResultState.observe(viewLifecycleOwner) {
            binding.menuInfoContainer.visibleOrGone(it == MenuDetailViewModel.State.SUCCESS)
            binding.onErrorContainer.root.visibleOrGone(it == MenuDetailViewModel.State.FAILED)
            binding.onLoadingContainer.root.visibleOrGone(it == MenuDetailViewModel.State.LOADING)
        }

        vm.menu.observe(viewLifecycleOwner) { menu ->
            // for marquee
            binding.menuTitle.isSelected = true
            binding.menuTitle.text = menu?.nameKr
            binding.menuRating.text = "${menu?.score ?: "0.0"} 점"
            binding.menuStars.rating = menu?.score?.toFloat() ?: 0.0f
            binding.reviewCount.text = "누적 평가 ${menu?.reviewCount ?: 0}개"
        }

        lifecycleScope.launch {
            vm.getReviews(args.menuId).collectLatest {
                reviewsAdapter.submitData(it)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
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
}
