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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.ReviewImageView
import com.wafflestudio.siksha2.compose.ui.menudetail.MenuRatingStars
import com.wafflestudio.siksha2.compose.ui.reviews.LeaveReviewRoute
import com.wafflestudio.siksha2.databinding.FragmentLeaveReviewBinding
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.utils.hasFinalConsInKr
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.launch

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

        binding.composeLayout.setContent {
            SikshaTheme {
                LeaveReviewRoute(
                    keywordTitleList = listOf("맛", "가격", "음식 구성"),
                    keywordChoiceLists = listOf(
                        listOf("아주좋아요", "굿", "그저그래요", "별로예요", "최악이에요"),
                        listOf("아주좋아요", "굿", "그저그래요", "별로예요", "최악이에요"),
                        listOf("아주좋아요", "굿", "그저그래요", "별로예요", "최악이에요")
                    ),
                    vm = vm,
                    onNavigateUp = {
                        findNavController().popBackStack()
                    },
                    onAddImage = {
                        requestPermission(onGranted = {
                            launchGalleryIntent()
                        })
                    },
                    onClickDetails = {},
                    context = context!!
                )
            }
        }

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

        vm.getRecommendationReview(vm.reviewRating.floatValue.toLong())
        binding.rateText.text = vm.reviewRating.floatValue.toLong().toString()
        binding.rating.setContent {
            MenuRatingStars(
                initialRating = 5f,
                changeEnabled = true,
                onRatingChange = { newRating ->
                    vm.setReviewRating(newRating)
                    binding.rateText.text = newRating.toLong().toString()
                    vm.getRecommendationReview(newRating.toLong())
                },
                width = 153.dp,
                height = 25.dp
            )
        }

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

        vm.leaveReviewState.observe(viewLifecycleOwner) {
            binding.onLoadingContainer.root.setVisibleOrGone(it == MenuDetailViewModel.ReviewState.COMPRESSING)
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch {
                val response = vm.leaveReview(
                    context = requireContext(),
                    score = vm.reviewRating.floatValue.toDouble(),
                    comment = binding.commentEdit.text.toString().ifEmpty {
                        binding.commentEdit.hint.toString()
                    }
                )
                when (response) {
                    is NetworkResult.Success -> {
                        // showToast(R.string.leave_review_success.toString())
                        showToast(getString(R.string.leave_review_success))
                        findNavController().popBackStack()
                    }
                    is NetworkResult.Failure -> {
                        showToast(response.message)
                    }
                    is NetworkResult.NetworkError -> {
                        showToast(getString(R.string.common_network_error))
                    }
                    else -> {
                        showToast(getString(R.string.common_unknown_error))
                    }
                }
                vm.notifySendReviewEnd()
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
