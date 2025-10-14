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
    private val vm: NotifyMenuViewModel by viewModels()

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
        binding.alarmTimeRow.setShowCheckSimple(false)
        binding.alarmMorningRow.setArrowIcon(false)
        binding.alarmMorningRow.setShowCheckSimple(true)

        binding.alarmTimeRow.setOnClickListener {
            binding.alarmTimeRow.setShowCheckSimple(true)
            binding.alarmMorningRow.setShowCheckSimple(false)
        }

        binding.alarmMorningRow.setOnClickListener {
            binding.alarmMorningRow.setShowCheckSimple(true)
            binding.alarmTimeRow.setShowCheckSimple(false)
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
