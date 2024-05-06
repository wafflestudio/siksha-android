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
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.ui.common.DefaultDialog
import com.wafflestudio.siksha2.ui.common.SikshaDialogListener
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.math.pow

@AndroidEntryPoint
class SettingFragment : Fragment(), SikshaDialogListener {

    private lateinit var binding: FragmentSettingBinding
    private val vm: SettingViewModel by activityViewModels()

    @Inject
    lateinit var userStatusManager: UserStatusManager

    private var packageVersion: String = BuildConfig.VERSION_NAME
    private var latestVersionNum: Int = 0

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

        lifecycleScope.launch {
            try {
                val version = userStatusManager.getVersion()
                latestVersionNum = 0
                version.split('.').forEachIndexed { idx, s ->
                    latestVersionNum += ((100.0F).pow(2 - idx) * (s.toIntOrNull() ?: 0)).toInt()
                }
                var currVersionNum = 0
                packageVersion.split('.').forEachIndexed { idx, s ->
                    currVersionNum += ((100.0F).pow(2 - idx) * (s.toIntOrNull() ?: 0)).toInt()
                }

                if (latestVersionNum == currVersionNum) {
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
                MainFragmentDirections.actionMainFragmentToSikshaInfoFragment(latestVersionNum.toLong())
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

        binding.logoutRow.setOnClickListener {
            // TODO: SikshaDialogController 만들기
            DefaultDialog.newInstance(getString(R.string.setting_dialog_logout_content))
                .show(childFragmentManager, "logout dialog")
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

    override fun onDialogNegativeClick() {}

    override fun onDialogPositiveClick() {
        val logoutCallback = { activity?.finish() }
        userStatusManager.logoutUser(requireContext(), logoutCallback)
    }
}
