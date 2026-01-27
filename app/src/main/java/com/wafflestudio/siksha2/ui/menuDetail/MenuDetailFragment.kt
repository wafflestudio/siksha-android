package com.wafflestudio.siksha2.ui.menuDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.menudetail.MenuDetailRoute
import com.wafflestudio.siksha2.databinding.FragmentMenuDetailBinding
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuDetailFragment : Fragment() {
    private val vm: MenuDetailViewModel by activityViewModels()

    private lateinit var binding: FragmentMenuDetailBinding

    private val args: MenuDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.refreshMenu(args.menuId)
        vm.refreshImages(args.menuId)
        vm.refreshReviewDistribution(args.menuId)
        vm.refreshKeywordDistribution(args.menuId)

        vm.menu.observe(viewLifecycleOwner) {
            binding.menuTitle.text = vm.menu.value?.nameKr
        }

        binding.composeLayout.setContent {
            SikshaTheme {
                MenuDetailRoute(
                    menuId = args.menuId,
                    vm = vm,
                    onToggleLikeMenu = {
                        vm.menu.value?.isLiked?.let {
                            viewLifecycleOwner.lifecycleScope.launch {
                                when (val response = vm.toggleLike(args.menuId, it)) {
                                    is NetworkResult.Success -> { }
                                    is NetworkResult.Failure -> showToast(response.message)
                                    is NetworkResult.NetworkError -> showToast(getString(R.string.common_network_error))
                                    else -> showToast(getString(R.string.common_unknown_error))
                                }
                            }
                        }
                    },
                    onToggleLikeReview = {
                        viewLifecycleOwner.lifecycleScope.launch {
                            when (val response = vm.toggleReviewLike(it)) {
                                is NetworkResult.Success -> { }
                                is NetworkResult.Failure -> showToast(response.message)
                                is NetworkResult.NetworkError -> showToast(getString(R.string.common_network_error))
                                else -> showToast(getString(R.string.common_unknown_error))
                            }
                        }
                    },
                    onClickLeaveReview = {
                        // if (args.isTodayMenu) {
                        val action =
                            MenuDetailFragmentDirections.actionMenuDetailFragmentToLeaveReviewFragment()
                        findNavController().navigate(action)
                        // } else {
                        // showToast("오늘 메뉴만 평가할 수 있습니다.")
                        // }
                    },
                    onNavigateToReviewPhoto = {
                        val action =
                            MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewPhotoFragment(args.menuId)
                        findNavController().navigate(action)
                    }
                )
            }
        }

        vm.networkResultState.observe(viewLifecycleOwner) {
            binding.composeLayout.setVisibleOrGone(it == MenuDetailViewModel.State.SUCCESS)
            binding.onErrorContainer.root.setVisibleOrGone(it == MenuDetailViewModel.State.FAILED)
            binding.onLoadingContainer.root.setVisibleOrGone(it == MenuDetailViewModel.State.LOADING)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
