package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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

        binding.dialogRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_yes -> {
                    // TODO: 별도 처리
                    binding.buttonDone.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.orange_500))
                }
                R.id.radio_no -> {
                    binding.buttonDone.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.orange_500))
                }
            }
        }

        binding.buttonDone.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "메뉴 알림 설정이 저장되었습니다",
                Toast.LENGTH_SHORT
            ).show()

            dismiss()
        }

        binding.buttonSetting.setOnClickListener {
            // TODO: 설정 화면 이동 처리
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.frame_corner_radius_10)
    }
}
