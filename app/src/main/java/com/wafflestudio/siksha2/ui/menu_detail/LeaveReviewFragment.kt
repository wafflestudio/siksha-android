package com.wafflestudio.siksha2.ui.menu_detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.wafflestudio.siksha2.utils.showToast
import com.wafflestudio.siksha2.utils.visibleOrGone
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber

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

        vm.uriList.observe(viewLifecycleOwner) {
            val reviewImageViewList = listOf(binding.reviewImageView1, binding.reviewImageView2, binding.reviewImageView3)

            for (i in 0 until 3) {
                if (i < it.size) {
                    reviewImageViewList[i].setImage(it[i])
                    reviewImageViewList[i].visibility = View.VISIBLE
                    reviewImageViewList[i].setOnDeleteClickListener(
                        object : ReviewImageView.OnDeleteClickListener {
                            override fun onClick() { vm.deleteUri(i) }
                        }
                    )
                } else {
                    reviewImageViewList[i].visibility = View.GONE
                }
            }

            binding.imageLayout.visibleOrGone(it.isNotEmpty())
        }

        vm.leaveReviewState.observe(viewLifecycleOwner) {
            binding.onLoadingContainer.root.visibleOrGone(it == MenuDetailViewModel.ReviewState.COMPRESSING)
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showToast("저장공간 권한이 없으면 사진을 업로할 수 없습니다.")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )

                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val comment = binding.commentEdit.text
                    showToast("이미지 압축 중입니다.")
                    vm.leaveReview(
                        requireContext(),
                        binding.rating.rating.toDouble(),
                        (
                            if (comment.isNullOrBlank()) binding.commentEdit.hint
                            else comment
                            ).toString()
                    )
                    showToast("평가가 등록되었습니다.")
                    vm.notifySendReviewEnd()
                    findNavController().popBackStack()
                } catch (e: HttpException) {
                    // TODO: 서버에 400 이 더 적절하지 않을 지 믈어보기
                    if (e.code() == 403) {
                        showToast("같은 메뉴에 리뷰를 여러 번 남길 수 없습니다.")
                        vm.notifySendReviewEnd()
                    }
                } catch (e: IOException) {
                    showToast("네트워크 연결이 불안정합니다.")
                    vm.notifySendReviewEnd()
                }
            }
        }

        binding.addImageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showToast("저장공간 권한이 없으면 사진을 등록할 수 없습니다.")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )

                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_GALLERY_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                if (!vm.addUri(it)) {
                    requireContext().showToast(getString(R.string.leave_review_max_image_toast))
                }
            }
        }
    }

    companion object {
        private const val GET_GALLERY_IMAGE = 1126
        private const val REQUEST_STORAGE_PERMISSION = 555
    }
}
