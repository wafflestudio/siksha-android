package com.wafflestudio.siksha2.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentMainBinding
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.ui.main.setting.favoriteMenu.FavoriteMenuAlarmDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var stateAdapter: FragmentStateAdapter

    @Inject
    lateinit var sikshaPrefs: SikshaPrefObjects

    private val vm: MainViewModel by activityViewModels()

    private var currentTabState = MainTabState.MAIN

    private var pendingShowToast = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (vm.shouldShowFavoriteModal()) {
            FavoriteMenuAlarmDialog { alarmEnabledIntent ->
                val notificationEnabled =
                    NotificationManagerCompat
                        .from(requireContext())
                        .areNotificationsEnabled()

                val actualEnabled =
                    if (alarmEnabledIntent) notificationEnabled else false

                vm.onAlarmPermissionSelected(actualEnabled)

                // 토스트 출력 시점 조절
                if (alarmEnabledIntent && !notificationEnabled) {
                    pendingShowToast = true
                } else {
                    Toast.makeText(
                        requireContext(),
                        "메뉴 알림 설정이 저장되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                vm.markFavoriteModalShown()
            }.show(parentFragmentManager, "FavoriteMenuAlarm")
        }

        initTab()
    }

    private fun initTab() {
        stateAdapter = MainFragmentStateAdapter(this)
        binding.viewPager.apply {
            adapter = stateAdapter
            isUserInputEnabled = false
            setCurrentItem(currentTabState.ordinal, false)
        }

        val tabIconIds = listOf(R.drawable.ic_tab_favorite, R.drawable.ic_tab_main, R.drawable.ic_tab_community, R.drawable.ic_tab_setting)
        tabIconIds.forEach { id ->
            val newTab = binding.tabLayout.newTab()

            binding.tabLayout.addTab(newTab)
        }

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab: TabLayout.Tab, i: Int ->
            val customView = LayoutInflater.from(context).inflate(R.layout.layout_tab_icon, null)
            customView.findViewById<ImageView>(R.id.content).setImageResource(tabIconIds[i])
            tab.customView = customView
        }.attach()
    }

    override fun onStop() {
        super.onStop()
        vm.setVpState(binding.viewPager.currentItem)
        currentTabState = MainTabState.fromPosition(binding.viewPager.currentItem)
    }

    override fun onResume() {
        super.onResume()

        if (pendingShowToast) {
            pendingShowToast = false

            Toast.makeText(
                requireContext(),
                "메뉴 알림 설정이 저장되었습니다",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
