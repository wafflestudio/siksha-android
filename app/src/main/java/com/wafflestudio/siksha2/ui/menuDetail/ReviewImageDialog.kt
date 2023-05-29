package com.wafflestudio.siksha2.ui.menuDetail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wafflestudio.siksha2.databinding.DialogReviewImageBinding
import com.wafflestudio.siksha2.utils.setImageUrl

class ReviewImageDialog(private val url: String) : DialogFragment() {
    private lateinit var binding: DialogReviewImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogReviewImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.reviewImage.setImageUrl(url)
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }
}
