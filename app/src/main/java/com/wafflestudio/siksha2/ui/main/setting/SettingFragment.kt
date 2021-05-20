package com.wafflestudio.siksha2.ui.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.databinding.FragmentSettingBinding
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.ui.SikshaDialog
import com.wafflestudio.siksha2.ui.SikshaDialogListener
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val vm: SettingViewModel by activityViewModels()

    @Inject
    lateinit var userStatusManager: UserStatusManager

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

        binding.infoRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToSikshaInfoFragment()
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

        lifecycleScope.launch {
            vm.showEmptyRestaurantFlow.collect {
                // 문구가 "메뉴없는 식당 숨기기" 임
                binding.showEmptyCheckRow.checked = it.not()
            }
        }
    }
}
