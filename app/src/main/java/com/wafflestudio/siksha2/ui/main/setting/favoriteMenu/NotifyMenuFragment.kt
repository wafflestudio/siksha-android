package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.databinding.FragmentNotifyMenuBinding
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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

        // 알림 토글 버튼
        binding.alarmToggleRow.setShowToggleSwitch(true)
        binding.alarmToggleRow.setArrowIcon(false)
        vm.setAlarmEnabled(prefs.alarmEnabled.getValue())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.groups
                    .combine(vm.loadState) { groups, loadState -> Pair(groups, loadState) }
                    .combine(vm.alarmEnabled) { (groups, loadState), alarmEnabled ->
                        Triple(groups, loadState, alarmEnabled)
                    }
                    .collect { (groups, loadState, alarmEnabled) ->
                        adapter.submitList(groups)
                        binding.alarmToggleRow.setToggleState(alarmEnabled)
                        updateMenuListVisibility(alarmEnabled, groups, loadState)
                    }
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

                // TODO: 서버 전체 알림 ON API 호출
                vm.setAlarmEnabled(enabled)
                prefs.alarmEnabled.setValue(true)
            } else {
                // OFF -> 서버 전체 알림 해제 API 호출
                vm.setAlarmEnabled(enabled)
                prefs.alarmEnabled.setValue(false)
                vm.disableAllAlarms(token)
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

    private fun updateMenuListVisibility(
        enabled: Boolean,
        groups: List<NotifyMenuGroupUiModel>,
        loadState: MenuLoadState
    ) {
        if (!enabled) {
            binding.menuGroupList.visibility = View.GONE
            binding.guideText.visibility = View.GONE
            binding.noMenuText.visibility = View.GONE
            return
        }

        when (loadState) {
            MenuLoadState.Idle,
            MenuLoadState.Loading -> {
                binding.menuGroupList.visibility = View.GONE
                binding.guideText.visibility = View.GONE
                binding.noMenuText.visibility = View.GONE
            }

            MenuLoadState.Loaded -> {
                val hasMenus = groups.isNotEmpty()

                binding.menuGroupList.visibility =
                    if (hasMenus) View.VISIBLE else View.GONE

                binding.guideText.visibility =
                    if (hasMenus) View.VISIBLE else View.GONE

                binding.noMenuText.visibility =
                    if (hasMenus) View.GONE else View.VISIBLE
            }
        }
    }
}
