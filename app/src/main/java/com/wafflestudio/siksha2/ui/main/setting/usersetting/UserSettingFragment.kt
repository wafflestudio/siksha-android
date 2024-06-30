package com.wafflestudio.siksha2.ui.main.setting.usersetting

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.wafflestudio.siksha2.databinding.FragmentSettingUsersettingBinding
import com.wafflestudio.siksha2.repositories.UserStatusManager


class UserSettingFragment : Fragment() {
    private lateinit var binding:FragmentSettingUsersettingBinding

    lateinit var userStatusManager: UserStatusManager

    lateinit var imageView:ShapeableImageView

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Glide.with(this).load(it).circleCrop().into(imageView)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            binding = FragmentSettingUsersettingBinding.inflate(inflater, container, false)
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val nickname = binding.nicknameSetRow.text.toString()
        }

    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }
}
