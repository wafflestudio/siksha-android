package com.wafflestudio.siksha2.ui.menuDetail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.reviews.LeaveReviewRoute
import com.wafflestudio.siksha2.databinding.FragmentLeaveReviewBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

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
            launchPhotoPicker()
        } else {
            showToast("사진 업로드를 위해 사진 권한을 허용해 주세요.")
        }
    }

    private val pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? =
        if (Build.VERSION.SDK_INT >= 33) {
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    vm.addImageUri(it, onFailure = {
                        requireContext().showToast(getString(R.string.leave_review_max_image_toast))
                    })
                }
            }
        } else {
            null
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
                        listOf("또 먹고 싶어요", "생각보다 맛있어요", "무난해요", "아쉬운 맛이에요", "별로예요"),
                        listOf("혜자스러워요", "가성비 좋아요", "합리적이에요", "약간 비싸요", "너무 비싸요"),
                        listOf("조화로워요", "알찬 편이에요", "기본적이에요", "다소 단조로워요", "너무 빈약해요")
                    ),
                    vm = vm,
                    onNavigateUp = {
                        findNavController().popBackStack()
                    },
                    onAddImage = {
                        requestPermission(onGranted = {
                            launchPhotoPicker()
                        })
                    },
                    onClickDetails = {},
                    context = requireContext()
                )
            }
        }

        vm.getRecommendationReview(vm.reviewRating.floatValue.toLong())

        vm.leaveReviewState.observe(viewLifecycleOwner) {
            binding.onLoadingContainer.root.setVisibleOrGone(it == MenuDetailViewModel.ReviewState.COMPRESSING)
        }
    }

    suspend fun downloadImageToFile(
        context: Context,
        imageUrl: String
    ): File? = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }

            val input = connection.inputStream
            val tempFile = File.createTempFile("review_img_", ".jpg", context.cacheDir)

            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }

            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun requestPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            onGranted()
            return
        }
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun launchPhotoPicker() {
        if (Build.VERSION.SDK_INT >= 33) {
            pickMedia!!.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            val intent = Intent(Intent.ACTION_PICK)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            galleryLauncher.launch(intent)
        }
    }

    companion object {
        private const val GET_GALLERY_IMAGE = 1126
        private const val REQUEST_STORAGE_PERMISSION = 555
    }
}
