package com.wafflestudio.siksha2.ui.main.setting.userAccount

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentUserProfileBinding
import com.wafflestudio.siksha2.ui.common.ImageBottomDialog
import com.wafflestudio.siksha2.ui.main.setting.SettingEvent
import com.wafflestudio.siksha2.ui.main.setting.SettingViewModel
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private val settingViewModel: SettingViewModel by activityViewModels()

    private lateinit var imageView: ShapeableImageView
    private var imageChanged: Boolean = false

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            settingViewModel.updateImageUri(it)
            Glide.with(this).load(it).circleCrop().into(imageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingViewModel.resetProfileUrlCache()

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
            settingViewModel.patchUserData(
                context = requireContext(),
                nickname = binding.nicknameSetRow.text.toString(),
                imageChanged = imageChanged
            )
        }

        binding.nicknameSetRow.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nicknameSetRow.hint = ""
            } else {
                binding.nicknameSetRow.hint = getString(R.string.nickname_hint)
            }
        }

        binding.cancelButton.setOnClickListener {
            binding.nicknameSetRow.setText(settingViewModel.userData.value?.nickname ?: "")
            hideKeyboard()
        }

        binding.okButton.setOnClickListener {
            hideKeyboard()
        }

        detectKeyboardVisibility()

        settingViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                binding.nicknameSetRow.setText(it.nickname)

                val imageUrl = it.profileUrl
                if (imageUrl != null) {
                    Glide.with(this).load(imageUrl).circleCrop().into(imageView)
                } else {
                    imageView.apply {
                        setImageResource(R.drawable.ic_rice_bowl)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.settingEvent.collect {
                    when (it) {
                        is SettingEvent.ChangeProfileSuccess -> {
                            findNavController().popBackStack()
                        }

                        is SettingEvent.ChangeProfileFailed -> {
                            showToast(it.errorMessage)
                        }
                    }
                }
            }
        }
    }

    private fun showImagePickerBottomDialog() {
        val bottomSheetFragment = ImageBottomDialog(
            onGallerySelected = { changeToGalleryImage() },
            onDefaultImageSelected = { changeToDefaultImage() }
        )
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
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

    private fun changeToGalleryImage() {
        pickImage.launch("image/*")
        imageChanged = true
    }

    private fun changeToDefaultImage() {
        settingViewModel.updateImageUri(null)
        imageView.apply {
            setImageResource(R.drawable.ic_rice_bowl)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        imageChanged = true
    }
}
