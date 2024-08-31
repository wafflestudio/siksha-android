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
        private const val ARG_DIALOG_TITLE = "ARG_DIALOG_TITLE"
        private const val ARG_DIALOG_CONTENT = "ARG_DIALOG_CONTENT"
        private const val ARG_DIALOG_POSITIVE = "ARG_DIALOG_POSITIVE"
        private const val ARG_DIALOG_NEGATIVE = "ARG_DIALOG_NEGATIVE"

        fun newInstance(title: CharSequence, content: CharSequence, positiveText: CharSequence, negativeText: CharSequence) =
            DefaultDialog().apply {
                arguments = Bundle().apply {
                    putCharSequence(ARG_DIALOG_TITLE, title)
                    putCharSequence(ARG_DIALOG_CONTENT, content)
                    putCharSequence(ARG_DIALOG_POSITIVE, positiveText)
                    putCharSequence(ARG_DIALOG_NEGATIVE, negativeText)
                }
            }
    }

    private var _binding: DialogDefaultBinding? = null
    private val binding: DialogDefaultBinding get() = _binding!!

    private var listener: DefaultDialogListener? = null

    private val title by lazy { arguments?.getCharSequence(ARG_DIALOG_TITLE) }
    private val content by lazy { arguments?.getCharSequence(ARG_DIALOG_CONTENT) }
    private val positiveText by lazy { arguments?.getCharSequence(ARG_DIALOG_POSITIVE) }
    private val negativeText by lazy { arguments?.getCharSequence(ARG_DIALOG_NEGATIVE) }

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
        listener = parentFragment as? DefaultDialogListener
            ?: activity as? DefaultDialogListener

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
        binding.apply {
            dialogTitle.text = title
            dialogContent.text = content
            tvPositiveButton.text = positiveText
            tvNegativeButton.text = negativeText
        }
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
