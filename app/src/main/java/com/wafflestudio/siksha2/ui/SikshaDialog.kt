package com.wafflestudio.siksha2.ui

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.wafflestudio.siksha2.databinding.DialogDefaultBinding

// TODO: 전반적으로 대충 짬 나중에 날잡고 고치기
class SikshaDialog : DialogFragment() {
    private lateinit var binding: DialogDefaultBinding

    private var listener: SikshaDialogListener? = null
    private val message by lazy { arguments?.getCharSequence(ARG_MESSAGE) }

    fun setListener(listener: SikshaDialogListener) { // TODO: 구성 변경 후에도 listener 유지되도록 고치기
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDefaultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val display = requireContext().display
        val size = Point()
        display?.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.content.text = message
        binding.positive.setOnClickListener { listener?.onPositive() }
        binding.negative.setOnClickListener { listener?.onNegative() }
    }

    companion object {
        private const val ARG_MESSAGE = "message"

        @JvmStatic
        fun newInstance(message: CharSequence) =
            SikshaDialog().apply {
                arguments = Bundle().apply {
                    putCharSequence(ARG_MESSAGE, message)
                }
            }
    }
}

interface SikshaDialogListener {
    fun onPositive()
    fun onNegative()
}
