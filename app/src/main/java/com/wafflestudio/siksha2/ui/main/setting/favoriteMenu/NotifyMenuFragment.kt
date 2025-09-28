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

        val token = prefs.accessToken.getValue()

        adapter = NotifyMenuAdapter { menuId, isChecked ->
            vm.onMenuChecked(menuId, isChecked, token)
        }

        vm.loadMenus(token)
        //vm.loadMockData()

        lifecycleScope.launchWhenStarted {
            vm.groups.collect { groups ->
                adapter.submitList(groups)
            }
        }

        // 알림 토글 버튼
        binding.alarmToggleRow.setShowSwitch(true)
        binding.alarmToggleRow.setSwitchChecked(true)
        binding.alarmToggleRow.setArrowIcon(false)

        // 토글 이벤트 처리
        binding.alarmToggleRow.setOnSwitchChangedListener { enabled ->
            if (enabled) {
                binding.guideText.visibility = View.VISIBLE
                binding.menuGroupList.visibility = View.VISIBLE
            } else {
                binding.guideText.visibility = View.GONE
                binding.menuGroupList.visibility = View.GONE
            }
        }

        // 메뉴 알림 시간
        binding.alarmTimeRow.setOnClickListener {
            val action = NotifyMenuFragmentDirections.actionNotifyMenuFragmentToNotifyTimeFragment()
            findNavController().navigate(action)
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

