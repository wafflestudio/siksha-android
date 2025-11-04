package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.DialogFavoriteMenuAlarmBinding

class FavoriteMenuAlarmDialog : DialogFragment() {
    private lateinit var binding: DialogFavoriteMenuAlarmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFavoriteMenuAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonDone.isEnabled = false

        binding.dialogRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_yes -> {
                    // TODO: 별도 처리
                    binding.buttonDone.isEnabled = true
                    binding.buttonDone.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.orange_500))
                }
                R.id.radio_no -> {
                    binding.buttonDone.isEnabled = true
                    binding.buttonDone.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.orange_500))
                }
            }
        }

        binding.buttonDone.setOnClickListener {
            if (!binding.buttonDone.isEnabled) return@setOnClickListener

            Toast.makeText(
                requireContext(),
                "메뉴 알림 설정이 저장되었습니다",
                Toast.LENGTH_SHORT
            ).show()

            dismiss()
        }

        binding.buttonSetting.setOnClickListener {
            dismiss()
            parentFragment?.findNavController()
                ?.navigate(R.id.action_mainFragment_to_notifyMenuFragment)
        }
    }

    override fun onStart() {
        super.onStart()

        val width = (360 * resources.displayMetrics.density).toInt()
        val height = (518 * resources.displayMetrics.density).toInt()
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.frame_corner_radius_16)
    }
}
