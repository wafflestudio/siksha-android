package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.databinding.FragmentNotifyTimeBinding
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotifyTimeFragment : Fragment() {
    private lateinit var binding: FragmentNotifyTimeBinding
    private val vm: NotifyTimeViewModel by viewModels()

    @Inject
    lateinit var prefs: SikshaPrefObjects

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = prefs.accessToken.getValue()

        // 알림 토글 버튼
        binding.alarmTimeRow.setArrowIcon(false)
        binding.alarmMorningRow.setArrowIcon(false)

        binding.alarmTimeRow.setShowCheckSimple(false)
        binding.alarmMorningRow.setShowCheckSimple(false)

        vm.loadAlarmType(token)
        vm.alarmType.observe(viewLifecycleOwner) { type ->
            when (type) {
                "EVERY_MEAL" -> {
                    binding.alarmTimeRow.setShowCheckSimple(true)
                    binding.alarmMorningRow.setShowCheckSimple(false)
                }
                else -> { // DAILY 또는 실패 시
                    binding.alarmMorningRow.setShowCheckSimple(true)
                    binding.alarmTimeRow.setShowCheckSimple(false)
                }
            }
        }

        binding.alarmTimeRow.setOnClickListener {
            binding.alarmTimeRow.setShowCheckSimple(true)
            binding.alarmMorningRow.setShowCheckSimple(false)

            vm.updateAlarmType("EVERY_MEAL", token)
        }

        binding.alarmMorningRow.setOnClickListener {
            binding.alarmMorningRow.setShowCheckSimple(true)
            binding.alarmTimeRow.setShowCheckSimple(false)

            vm.updateAlarmType("DAILY", token)
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
