package com.wafflestudio.siksha2.ui.menuDetail.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.databinding.FragmentMyReviewBinding
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.network.dto.ReviewRestaurant
import com.wafflestudio.siksha2.ui.menuDetail.MenuDetailViewModel
import com.wafflestudio.siksha2.ui.menuDetail.MenuMyReviewAdapter
import kotlinx.coroutines.launch
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
        reviewsAdapter = MenuMyReviewAdapter()

        // RecyclerView 연결
        binding.myReviewList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewsAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val mockData = createMockReviews()
            val pagingData = PagingData.from(mockData)
            reviewsAdapter.submitData(pagingData)
        }

        /* 데이터 수집 (PagingData)
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
        }*/

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun createMockReviews(): List<ReviewRestaurant> {
        val now = "2025-10-04T12:00:00Z"

        val review1 = Review(
            id = 1L,
            menuId = 101L,
            userId = 999L,
            score = 4.5,
            comment = "양이 많고 맛있었어요! 재방문 의사 있습니다 😊",
            createdAt = now,
            updatedAt = now,
            etc = null
        )

        val review2 = Review(
            id = 2L,
            menuId = 102L,
            userId = 999L,
            score = 3.8,
            comment = "맛은 괜찮았지만 조금 짰어요.",
            createdAt = now,
            updatedAt = now,
            etc = null
        )

        val review3 = Review(
            id = 3L,
            menuId = 103L,
            userId = 999L,
            score = 5.0,
            comment = "완벽한 디저트! 꼭 드셔보세요 🍰",
            createdAt = now,
            updatedAt = now,
            etc = null
        )

        val restaurant1 = ReviewRestaurant(
            restaurantId = "r001",
            nameKr = "한남돈까스",
            nameEn = "Hannam Pork Cutlet",
            reviews = listOf(review1, review2)
        )

        val restaurant2 = ReviewRestaurant(
            restaurantId = "r002",
            nameKr = "카페 달빛",
            nameEn = "Cafe Moonlight",
            reviews = listOf(review3)
        )

        return listOf(restaurant1, restaurant2)
    }
}
