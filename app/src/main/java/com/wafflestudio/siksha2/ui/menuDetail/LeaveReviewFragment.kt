package com.wafflestudio.siksha2.ui.menuDetail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                vm.addImageUri(it, onFailure = {
                    requireContext().showToast(getString(R.string.leave_review_max_image_toast))
                })
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchGalleryIntent()
        } else {
            showToast("사진 업로드를 위해 사진 권한을 허용해 주세요.")
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

        vm.imageUriList.observe(viewLifecycleOwner) { imageUriList ->
            binding.imageLayout.forEachIndexed { index, view ->
                (view as ReviewImageView).run {
                    if (index < imageUriList.size) {
                        setImage(imageUriList[index])
                        visibility = View.VISIBLE
                        setOnDeleteClickListener(
                            object : ReviewImageView.OnDeleteClickListener {
                                override fun onClick() {
                                    vm.deleteImageUri(index)
                                }
                            }
                        )
                    } else {
                        visibility = View.GONE
                    }
                }
            }
            binding.imageLayout.setVisibleOrGone(imageUriList.isNotEmpty())
        }

        vm.leaveLeaveReviewState.observe(viewLifecycleOwner) {
            binding.onLoadingContainer.root.setVisibleOrGone(it == LeaveReviewState.COMPRESSING)
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    vm.leaveReview(
                        context = requireContext(),
                        score = binding.rating.rating.toDouble(),
                        comment = binding.commentEdit.text.toString().ifEmpty {
                            binding.commentEdit.hint.toString()
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
            requestPermission(onGranted = {
                launchGalleryIntent()
            })
        }
    }

    private fun requestPermission(onGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
            .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        galleryLauncher.launch(intent)
    }

    companion object {
        private const val GET_GALLERY_IMAGE = 1126
        private const val REQUEST_STORAGE_PERMISSION = 555
    }
}
