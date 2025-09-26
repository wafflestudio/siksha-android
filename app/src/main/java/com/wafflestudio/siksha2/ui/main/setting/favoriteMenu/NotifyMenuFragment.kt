package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.wafflestudio.siksha2.databinding.FragmentNotifyMenuBinding
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotifyMenuFragment : Fragment() {
    private lateinit var binding: FragmentNotifyMenuBinding
    private val vm: NotifyMenuViewModel by viewModels()
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

        adapter = NotifyMenuAdapter { menuId, isChecked ->
            vm.onMenuChecked(menuId, isChecked)
        }

        val token = prefs.accessToken.getValue()

        vm.loadMenus(token)
        //vm.loadMockData()

        lifecycleScope.launchWhenStarted {
            vm.groups.collect { groups ->
                adapter.submitList(groups)

                if (groups.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                    binding.guideText.visibility = View.GONE
                } else {
                    binding.emptyText.visibility = View.GONE
                    binding.guideText.visibility = View.VISIBLE
                }
            }
        }

        // 알림 토글 버튼
        binding.alarmToggleRow.setShowSwitch(true)
        binding.alarmToggleRow.setSwitchChecked(true)
        // 토글 이벤트 처리
        binding.alarmToggleRow.setOnSwitchChangedListener { enabled ->
            if (enabled) {
                // 알림 ON 로직
            } else {
                binding.guideText.visibility = View.GONE
                binding.menuGroupList.visibility = View.GONE
            }
        }

        // 알림 받기 버튼
        binding.btnReceiveAlarm.setOnClickListener {
            vm.saveAlarms(token)
            Toast.makeText(requireContext(), "메뉴 알림 설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()

            val action = NotifyMenuFragmentDirections.actionNotifyMenuFragmentToFavoriteMenuFragment()
            findNavController().navigate(action)
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

