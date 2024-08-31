package com.wafflestudio.siksha2.ui.main.setting.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentUserAccountBinding
import com.wafflestudio.siksha2.ui.common.DefaultDialog
import com.wafflestudio.siksha2.ui.common.DefaultDialogListener
import com.wafflestudio.siksha2.ui.main.setting.SettingViewModel
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okio.IOException

@AndroidEntryPoint
class UserAccountFragment : Fragment(), DefaultDialogListener {

    private lateinit var binding: FragmentUserAccountBinding

    private val vm: SettingViewModel by activityViewModels()

    private var isLogoutAction = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (vm.versionCheck.value == true) {
            binding.versionCheckText.text = getString(R.string.setting_using_latest_version)
        } else {
            binding.versionCheckText.text = getString(R.string.setting_need_update)
        }

        binding.versionText.text = getString(R.string.version_text, vm.packageVersion)

        initOnClickListener()
    }

    private fun initOnClickListener() {
        with(binding) {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            logoutRow.setOnClickListener {
                isLogoutAction = true
                DefaultDialog.newInstance(
                    getString(R.string.setting_dialog_logout_title),
                    getString(R.string.setting_dialog_logout_content),
                    getString(R.string.setting_dialog_logout_proceed),
                    getString(R.string.setting_dialog_logout_cancel)
                )
                    .show(childFragmentManager, "logout dialog")
            }

            withdrawalRow.setOnClickListener {
                // TODO: SikshaDialogController 만들기
                isLogoutAction = false
                DefaultDialog.newInstance(
                    getString(R.string.setting_dialog_withdrawal_title),
                    getString(R.string.setting_dialog_withdrawal_content),
                    getString(R.string.setting_dialog_withdrawal_proceed),
                    getString(R.string.setting_dialog_withdrawal_cancel)
                )
                    .show(childFragmentManager, "withdrawal dialog")
            }
        }
    }

    override fun onDialogNegativeClick() {}

    override fun onDialogPositiveClick() {
        lifecycleScope.launch {
            try {
                if (isLogoutAction) {
                    val logoutCallback: () -> Unit = { activity?.finish() }
                    vm.logoutUser(requireContext(), logoutCallback)
                } else {
                    val withdrawCallback: () -> Unit = { activity?.finish() }
                    vm.deleteUser(requireContext(), withdrawCallback)
                }
            } catch (e: IOException) {
                showToast(getString(R.string.common_network_error))
            }
        }
    }
}
