package com.wafflestudio.siksha2.ui.menu_detail

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.OnRatingChangeListener
import com.wafflestudio.siksha2.databinding.FragmentLeaveReviewBinding
import com.wafflestudio.siksha2.utils.hasFinalConsInKr
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class LeaveReviewFragment : Fragment() {
    private lateinit var binding: FragmentLeaveReviewBinding
    private val vm: MenuDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaveReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.menu.observe(viewLifecycleOwner) { menu ->
            menu?.let {
                binding.menuTitle.text = it.nameKr
                binding.menuTitleHowAbout.text = when (it.nameKr?.hasFinalConsInKr()) {
                    true -> getString(R.string.leave_review_how_about_with_bottom)
                    false -> getString(R.string.leave_review_how_about_wo_bottom)
                    else -> getString(R.string.leave_review_how_about_wo_bottom)
                }
            }
        }

        vm.commentHint.observe(viewLifecycleOwner) { hint ->
            binding.commentEdit.hint = hint
        }

        binding.commentEdit.filters = binding.commentEdit.filters + InputFilter.LengthFilter(150)
        binding.textCount.text = getString(
            R.string.leave_review_text_count,
            0,
            150
        )

        binding.commentEdit.addTextChangedListener {
            binding.textCount.text = getString(
                R.string.leave_review_text_count,
                it?.length,
                150
            )
        }

        vm.getRecommendationReview(binding.rating.rating.toLong())
        binding.rateText.text = binding.rating.rating.toLong().toString()
        binding.rating.setOnRatingChangeListener(
            object : OnRatingChangeListener {
                override fun onChange(rating: Float) {
                    vm.getRecommendationReview(rating.toLong())
                    binding.rateText.text = rating.toLong().toString()
                }
            }
        )

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val comment = binding.commentEdit.text
                    vm.leaveReview(
                        binding.rating.rating.toDouble(),
                        (
                            if (comment.isNullOrBlank()) binding.commentEdit.hint
                            else comment
                            ).toString()
                    )
                    showToast("평가가 등록되었습니다.")
                    findNavController().popBackStack()
                } catch (e: HttpException) {
                    // TODO: 서버에 400 이 더 적절하지 않을 지 믈어보기
                    if (e.code() == 403) {
                        showToast("같은 메뉴에 리뷰를 여러 번 남길 수 없습니다.")
                    }
                } catch (e: IOException) {
                    showToast("네트워크 연결이 불안정합니다.")
                }
            }
        }
    }
}
