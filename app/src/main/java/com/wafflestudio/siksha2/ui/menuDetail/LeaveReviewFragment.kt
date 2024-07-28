package com.wafflestudio.siksha2.ui.menuDetail

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEachIndexed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.OnRatingChangeListener
import com.wafflestudio.siksha2.components.ReviewImageView
import com.wafflestudio.siksha2.databinding.FragmentLeaveReviewBinding
import com.wafflestudio.siksha2.utils.hasFinalConsInKr
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class LeaveReviewFragment : Fragment() {
    private lateinit var binding: FragmentLeaveReviewBinding

    private val vm: MenuDetailViewModel by activityViewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        val context = context ?: return@registerForActivityResult
        uris.forEach { uri ->
            vm.addImageUri(
                context = context,
                imageUri = uri,
                onFailure = {
                    requireContext().showToast(getString(R.string.leave_review_max_image_toast))
                }
            )
        }
    }

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

        vm.refreshUriList()

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

        viewLifecycleOwner.lifecycleScope.launch {
            vm.images.collect { imageUriList ->
                binding.imageLayout.forEachIndexed { index, view ->
                    val frameLayout = view as? FrameLayout ?: return@forEachIndexed
                    val reviewImageView = frameLayout.getChildAt(0) as? ReviewImageView ?: return@forEachIndexed
                    val progressBar = frameLayout.getChildAt(1) as? ProgressBar ?: return@forEachIndexed

                    if (index >= imageUriList.size) {
                        reviewImageView.visibility = View.GONE
                        return@forEachIndexed
                    }

                    reviewImageView.visibility = View.VISIBLE
                    reviewImageView.setOnDeleteClickListener(
                        object : ReviewImageView.OnDeleteClickListener {
                            override fun onClick() {
                                vm.deleteImageUri(index)
                            }
                        }
                    )

                    when (val imageState = imageUriList[index]) {
                        is CompressedImageUiState.Compressing -> {
                            reviewImageView.setImage(imageState.originalImageUri)
                            progressBar.visibility = View.VISIBLE
                        }

                        is CompressedImageUiState.Completed -> {
                            reviewImageView.setImage(imageState.compressedImageUri)
                            progressBar.visibility = View.GONE
                        }
                    }

                }
                binding.imageLayout.setVisibleOrGone(imageUriList.isNotEmpty())
            }
        }

        vm.leaveReviewState.observe(viewLifecycleOwner) {
            binding.onLoadingContainer.root.setVisibleOrGone(it == MenuDetailViewModel.ReviewState.COMPRESSING)
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    vm.leaveReview(
                        score = binding.rating.rating.toDouble(),
                        comment = binding.commentEdit.text.toString().ifEmpty {
                            binding.commentEdit.hint.toString()
                        },
                        onFailure = {
                            showToast("이미지 압축 중입니다.")
                            return@leaveReview
                        }
                    )
                    showToast("평가가 등록되었습니다.")
                    findNavController().popBackStack()
                } catch (e: HttpException) {
                    // TODO: 서버에 400 이 더 적절하지 않을 지 믈어보기
                    if (e.code() == 403) {
                        showToast("같은 메뉴에 리뷰를 여러 번 남길 수 없습니다.")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("네트워크 연결이 불안정합니다.")
                } finally {
                    vm.notifySendReviewEnd()
                }
            }
        }

        binding.addImageButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}
