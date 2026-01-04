package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.DialogFavoriteMenuAlarmBinding
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

class FavoriteMenuAlarmDialog(
    private val onResult: (
        alarmEnabled: Boolean
    ) -> Unit
) : DialogFragment() {
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
        var alarmEnabledSelected = false

        binding.dialogRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_yes -> {
                    binding.buttonDone.isEnabled = true
                    alarmEnabledSelected = true
                    binding.buttonDone.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.orange_500))
                }
                R.id.radio_no -> {
                    binding.buttonDone.isEnabled = true
                    alarmEnabledSelected = false
                    binding.buttonDone.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.orange_500))
                }
            }
        }

        binding.buttonDone.setOnClickListener {
            if (!binding.buttonDone.isEnabled) return@setOnClickListener

            // 안드로이드 알림 설정으로 이동
            if (alarmEnabledSelected && !isNotificationEnabled()) {
                openNotificationSettings()
            }

            // parentFragment가 prefs.alarmEnabled를 저장하도록 전달
            onResult(alarmEnabledSelected)

            dismiss()
        }

        binding.buttonSetting.setOnClickListener {
            dismiss()
            parentFragment?.findNavController()
                ?.navigate(R.id.action_mainFragment_to_notifyMenuFragment)
        }
    }

    private fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
    }

    private fun openNotificationSettings() {
        val context = requireContext()
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            putExtra("app_package", context.packageName)
            putExtra("app_uid", context.applicationInfo.uid)
        }
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()

        val width = (360 * resources.displayMetrics.density).toInt()
        val height = (518 * resources.displayMetrics.density).toInt()
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.frame_corner_radius_16)
    }
}
