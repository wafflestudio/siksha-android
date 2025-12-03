package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.databinding.FragmentNotifyMenuBinding
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotifyMenuFragment : Fragment() {
    private lateinit var binding: FragmentNotifyMenuBinding
    private val vm: NotifyMenuViewModel by activityViewModels()

    @Inject
    lateinit var prefs: SikshaPrefObjects

    private lateinit var adapter: NotifyMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = prefs.accessToken.getValue()

        adapter = NotifyMenuAdapter { menuId, isChecked ->
            vm.onMenuChecked(menuId, isChecked, token)
        }

        binding.menuGroupList.layoutManager = LinearLayoutManager(requireContext())
        binding.menuGroupList.adapter = adapter

        vm.loadMenus(token)

        var alarmEnabled = prefs.alarmEnabled.getValue()
        Log.d("NotifyMenuFragment", "alarmEnabled: $alarmEnabled")

        // 알림 토글 버튼
        binding.alarmToggleRow.setShowToggleSwitch(true)
        binding.alarmToggleRow.setToggleState(alarmEnabled)
        binding.alarmToggleRow.setArrowIcon(false)

        updateMenuListVisibility(alarmEnabled)

        lifecycleScope.launchWhenStarted {
            vm.groups.collect { groups ->
                adapter.submitList(groups)
                updateMenuListVisibility(alarmEnabled)
            }
        }

        // 토글 이벤트 처리
        binding.alarmToggleRow.setOnToggleClicked { enabled ->
            if (enabled) {
                // OS 알림 허용 체크
                if (!isNotificationEnabled()) {
                    binding.alarmToggleRow.setToggleState(false)
                    openNotificationSettings()
                    return@setOnToggleClicked
                }

                alarmEnabled = true
                prefs.alarmEnabled.setValue(true)
                updateMenuListVisibility(true)

            } else {
                // OFF → 서버 전체 알림 해제 API 호출
                alarmEnabled = false
                prefs.alarmEnabled.setValue(false)
                vm.disableAllAlarms(token)
                updateMenuListVisibility(false)
            }
        }

        // 메뉴 알림 시간 화면으로 이동
        binding.alarmTimeRow.setOnClickListener {
            val action = NotifyMenuFragmentDirections.actionNotifyMenuFragmentToNotifyTimeFragment()
            findNavController().navigate(action)
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        }
        startActivity(intent)
    }

    private fun updateMenuListVisibility(enabled: Boolean) {
        if (!enabled) {
            binding.menuGroupList.visibility = View.GONE
            binding.guideText.visibility = View.GONE
            binding.noMenuText.visibility = View.GONE
            return
        }

        val hasMenus = adapter.currentList.isNotEmpty()
        Log.d("NotifyMenuFragment", "hasMenus: $hasMenus")
        binding.menuGroupList.visibility = View.VISIBLE
        binding.guideText.visibility =
            if (hasMenus) View.VISIBLE else View.INVISIBLE
        binding.noMenuText.visibility =
            if (hasMenus) View.INVISIBLE else View.VISIBLE
    }
}
