package com.wafflestudio.siksha2.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.DialogProfileImageBinding

class ImageBottomDialog(
    private val onGallerySelected: () -> Unit,
    private val onDefaultImageSelected: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogProfileImageBinding.inflate(inflater, container, false)

        binding.albumRow.setOnClickListener {
            onGallerySelected()
            dismiss()
        }

        binding.defaultImageRow.setOnClickListener {
            onDefaultImageSelected()
            dismiss()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme)
    }
}
