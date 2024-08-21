package com.wafflestudio.siksha2.ui.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.BuildConfig
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentSettingBinding
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val vm: SettingViewModel by activityViewModels()

    private val packageVersion: String = BuildConfig.VERSION_NAME

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.versionText.text = "siksha-" + packageVersion

        vm.userData.observe(viewLifecycleOwner) { user ->
            binding.nickname.text = user.nickname
        }

        lifecycleScope.launch {
            try {
                if (vm.versionCheck(packageVersion)) {
                    binding.versionCheckText.text = getString(R.string.setting_using_latest_version)
                } else {
                    binding.versionCheckText.text = getString(R.string.setting_need_update)
                }
            } catch (e: IOException) {
                showToast("최신버전 정보를 가져올 수 없습니다.")
            }
        }

        binding.infoRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToUserSettingFragment2()
            findNavController().navigate(action)
        }

        binding.orderRestaurantRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToReorderRestaurantFragment()
            findNavController().navigate(action)
        }

        binding.orderFavoriteRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToReorderRestaurantFragment()
            findNavController().navigate(action)
        }

        binding.showEmptyCheckRow.setOnClickListener {
            vm.toggleShowEmptyRestaurant()
        }

        binding.settingAccountRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToSettingAccountFragment(latestVersionNum.toLong())
            findNavController().navigate(action)
        }

        binding.vocRow.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToVocFragment()
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            vm.showEmptyRestaurantFlow.collect {
                // 문구가 "메뉴없는 식당 숨기기" 임
                binding.showEmptyCheckRow.checked = it.not()
            }
        }
    }
}
