package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        binding.menuGroupList.layoutManager = LinearLayoutManager(requireContext())
        binding.menuGroupList.adapter = adapter

        vm.loadMenus(token)
        // vm.loadMockData()

        // 알림 토글 버튼
        binding.alarmToggleRow.setShowToggleSwitch(true)
        binding.alarmToggleRow.setToggleState(false) // TODO: 실제 상태 반영하기
        binding.alarmToggleRow.setArrowIcon(false)

        var alarmEnabled = false // TODO: 실제 상태 반영하기

        lifecycleScope.launchWhenStarted {
            vm.groups.collect { groups ->
                adapter.submitList(groups)

                if (alarmEnabled) {
                    val hasMenus = groups.isNotEmpty()
                    binding.menuGroupList.visibility = View.VISIBLE
                    binding.guideText.visibility = if (hasMenus) View.VISIBLE else View.INVISIBLE
                    binding.noMenuText.visibility = if (hasMenus) View.INVISIBLE else View.VISIBLE
                }
            }
        }

        // 토글 이벤트 처리
        binding.alarmToggleRow.setOnToggleClicked { enabled ->
            alarmEnabled = enabled

            if (enabled) {
                val hasMenus = adapter.currentList.isNotEmpty()
                binding.menuGroupList.visibility = View.VISIBLE
                binding.guideText.visibility = if (hasMenus) View.VISIBLE else View.INVISIBLE
                binding.noMenuText.visibility = if (hasMenus) View.INVISIBLE else View.VISIBLE
            } else {
                binding.menuGroupList.visibility = View.GONE
                binding.guideText.visibility = View.GONE
                binding.noMenuText.visibility = View.GONE
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
