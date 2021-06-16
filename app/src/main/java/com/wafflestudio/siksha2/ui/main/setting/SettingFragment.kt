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
import com.wafflestudio.siksha2.ui.SikshaDialog
import com.wafflestudio.siksha2.ui.SikshaDialogListener
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.math.*

@AndroidEntryPoint
class SettingFragment : Fragment() {
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
                MainFragmentDirections.actionMainFragmentToReorderRestaurantFragment(false)
            findNavController().navigate(action)
        }

        binding.orderFavoriteRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToReorderRestaurantFragment(true)
            findNavController().navigate(action)
        }

        binding.showEmptyCheckRow.setOnClickListener {
            vm.toggleShowEmptyRestaurant()
        }

        binding.logoutRow.setOnClickListener {
            // TODO: SikshaDialogController 만들기
            val dialog = SikshaDialog("정말로 로그아웃 하시겠습니까?")
            dialog.setListener(
                object : SikshaDialogListener {
                    override fun onPositive() {
                        val logoutCallback = { activity?.finish() }
                        userStatusManager.logoutUser(requireContext(), logoutCallback)
                    }

                    override fun onNegative() {
                        dialog.dismiss()
                    }
                }
            )
            dialog.show(childFragmentManager, "logout")
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
