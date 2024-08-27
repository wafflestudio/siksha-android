package com.wafflestudio.siksha2.ui.main.setting.userAccount

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.DialogProfileImageBinding
import com.wafflestudio.siksha2.databinding.FragmentSettingUsersettingBinding
import com.wafflestudio.siksha2.ui.main.setting.SettingViewModel
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentSettingUsersettingBinding
    private val userSettingViewModel: SettingViewModel by activityViewModels()

    private lateinit var imageView: ShapeableImageView

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            userSettingViewModel.updateImageUri(it)
            Glide.with(this).load(it).circleCrop().into(imageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            binding = FragmentSettingUsersettingBinding.inflate(inflater, container, false)
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = binding.imageView

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageView.setOnClickListener {
            showImagePickerBottomDialog()
        }

        binding.eraseButton.setOnClickListener {
            binding.nicknameSetRow.text.clear()
        }

        binding.completeButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    userSettingViewModel.patchUserData(
                        context = requireContext(),
                        nickname = binding.nicknameSetRow.text.toString()
                    )
                    findNavController().popBackStack()
                } catch (e: HttpException) {
                    if (e.code() == 409) {
                        showToast("이미 존재하는 닉네임입니다.")
                    }
                } catch (e: IOException) {
                    showToast("오류가 발생했습니다.")
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            binding.nicknameSetRow.setText(userSettingViewModel.userData.value?.nickname ?: "")
            hideKeyboard()
        }

        binding.okButton.setOnClickListener {
            hideKeyboard()
        }

        detectKeyboardVisibility()

        userSettingViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                binding.nicknameSetRow.setText(it.nickname)

                it.profileUrl?.let { uri ->
                    Glide.with(this).load(uri).circleCrop().into(imageView)
                }
            }
        }
    }

    private fun applyDefaultImage() {
        imageView.setImageResource(R.drawable.ic_rice_bowl)
        // 1. Server에 image 기본 rice_bowl image 전송
        // 2. isDefaultImage를 true로 변경
        // 추후 서버팀 선택에 따라 추가 작업 필요
    }

    private fun showImagePickerBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme)
        val dialogBinding = DialogProfileImageBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.albumButton.setOnClickListener {
            openGallery()
            bottomSheetDialog.dismiss()
        }

        dialogBinding.defaultImageButton.setOnClickListener {
            applyDefaultImage()
            bottomSheetDialog.dismiss()
        }

        dialogBinding.cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun detectKeyboardVisibility() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // When keyboard is visible
                binding.completeButton.visibility = View.GONE
                binding.cancelOkLayout.visibility = View.VISIBLE
            } else {
                // When keyboard is hidden
                binding.completeButton.visibility = View.VISIBLE
                binding.cancelOkLayout.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }
}
