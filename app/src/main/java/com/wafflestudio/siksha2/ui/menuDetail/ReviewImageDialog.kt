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

class ReviewImageDialog : DialogFragment() {

    companion object {
        private const val ARG_URL = "ARG_URL"

        @JvmStatic
        fun newInstance(url: String) =
            ReviewImageDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                }
            }
    }

    private var _binding: DialogReviewImageBinding? = null
    private val binding get() = _binding!!
    private val url by lazy {
        arguments?.getString(ARG_URL)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReviewImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initView()
        initClickListener()
    }

    private fun initView() {
        binding.ivReviewImage.setImageUrl(url)
    }

    private fun initClickListener() {
        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
