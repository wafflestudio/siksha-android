package com.wafflestudio.siksha2.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wafflestudio.siksha2.databinding.DialogDefaultBinding

class DefaultDialog : DialogFragment() {

    companion object {
        private const val ARG_CONTENT = "ARG_CONTENT"

        fun newInstance(message: CharSequence) =
            DefaultDialog().apply {
                arguments = Bundle().apply {
                    putCharSequence(ARG_CONTENT, message)
                }
            }
    }

    private var _binding: DialogDefaultBinding? = null
    private val binding: DialogDefaultBinding get() = _binding!!

    private var listener: SikshaDialogListener? = null

    private val content by lazy { arguments?.getCharSequence(ARG_CONTENT) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDefaultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initView()
        initClickListener()
    }

    private fun initClickListener() {
        listener = parentFragment as? SikshaDialogListener
            ?: activity as? SikshaDialogListener

        with(binding) {
            tvNegativeButton.setOnClickListener {
                dismiss()
                listener?.onDialogNegativeClick()
            }
            tvPositiveButton.setOnClickListener {
                listener?.onDialogPositiveClick()
                dismiss()
            }
        }
    }

    private fun initView() {
        binding.tvContent.text = content
    }

    override fun onResume() {
        super.onResume()
        resizeDialogWidth()
    }

    private fun resizeDialogWidth() {
        val params = dialog?.window?.attributes
        params?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.attributes = params
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
