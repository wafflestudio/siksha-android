package com.wafflestudio.siksha2.ui.main.setting.userAccount

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
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
            openGallery()
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

        userSettingViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                binding.nicknameSetRow.setText(it.nickname)

                it.profileUrl?.let { uri ->
                    Glide.with(this).load(uri).circleCrop().into(imageView)
                }
            }
        }
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }
}
