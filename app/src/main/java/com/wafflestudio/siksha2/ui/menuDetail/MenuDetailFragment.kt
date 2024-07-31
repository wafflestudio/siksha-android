package com.wafflestudio.siksha2.ui.menuDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wafflestudio.siksha2.compose.ui.menuDetail.MenuDetailRoute
import com.wafflestudio.siksha2.databinding.FragmentMenuDetailBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import dagger.hilt.android.AndroidEntryPoint

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
        binding.menuDetailComposeView.setContent {
            SikshaTheme {
                MenuDetailRoute(
                    menuId = args.menuId,
                    isTodayMenu = args.isTodayMenu,
                    onNavigateUp = {
                        findNavController().popBackStack()
                    },
                    onNavigateToLeaveReview = {
                        findNavController().navigate(
                            MenuDetailFragmentDirections.actionMenuDetailFragmentToLeaveReviewFragment() // TODO: leaveReviewFragment로 이동 시 menuId arguement 전달하는 식으로 바꾸기
                        )
                    },
                    onNavigateToReviewPhoto = { menuId ->
                        findNavController().navigate(
                            MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewPhotoFragment(menuId)
                        )
                    },
                    onNavigateToReview = { menuId ->
                        findNavController().navigate(
                            MenuDetailFragmentDirections.actionMenuDetailFragmentToReviewFragment(menuId)
                        )
                    },
                    menuDetailViewModel = vm
                )
            }
        }
    }
}
