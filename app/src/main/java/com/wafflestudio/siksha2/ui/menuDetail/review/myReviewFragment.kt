package com.wafflestudio.siksha2.ui.menuDetail.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlin.getValue

class MyReviewFragment: Fragment() {
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

        // 데이터 수집 (PagingData)
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

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
