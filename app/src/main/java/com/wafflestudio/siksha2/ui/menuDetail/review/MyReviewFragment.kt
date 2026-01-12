package com.wafflestudio.siksha2.ui.menuDetail.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.databinding.FragmentMyReviewBinding
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.ui.menuDetail.MenuMyReviewAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.getValue

class MyReviewFragment : Fragment() {
    private lateinit var binding: FragmentMyReviewBinding
    private val vm: MenuDetailViewModel by activityViewModels()
    private lateinit var reviewsAdapter: MenuMyReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter 초기화
        reviewsAdapter = MenuMyReviewAdapter(
            onMenuClick = { review ->
                val action = MyReviewFragmentDirections.actionMyReviewFragmentToMenuDetailFragment(
                    review.menuId,
                    Instant.parse(review.createdAt).atZone(ZoneId.systemDefault()).toLocalDate() == LocalDate.now()
                )
                findNavController().navigate(action)
            },
            onEditClick = { review ->
                vm.refreshMenu(review.menuId)
                vm.setEditingReview(requireContext(), review)

                val action = MyReviewFragmentDirections
                    .actionMyReviewFragmentToLeaveReviewFragment()
                findNavController().navigate(action)
            },
            onDeleteClick = { review ->
                vm.deleteReview(review.id)
            }
        )

        // RecyclerView 연결
        binding.myReviewList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewsAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.getMyReviews().collectLatest { pagingData ->
                reviewsAdapter.submitData(pagingData)
            }
        }

        // 로딩 / 에러 상태 UI 처리 (optional)
        viewLifecycleOwner.lifecycleScope.launch {
            reviewsAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.textNoReviews.isVisible = loadStates.refresh is LoadState.Error
            }
        }

        vm.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()
                reviewsAdapter.refresh()
            } else {
                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
