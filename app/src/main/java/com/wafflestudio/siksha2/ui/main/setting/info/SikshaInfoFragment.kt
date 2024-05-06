package com.wafflestudio.siksha2.ui.main.setting.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wafflestudio.siksha2.BuildConfig
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentSikshaInfoBinding
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.ui.common.DefaultDialog
import com.wafflestudio.siksha2.ui.common.SikshaDialogListener
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject
import kotlin.math.pow

@AndroidEntryPoint
class SikshaInfoFragment : Fragment(), SikshaDialogListener {

    private var _binding: FragmentSikshaInfoBinding? = null
    private val binding get() = _binding!!

    private val args: SikshaInfoFragmentArgs by navArgs()

    @Inject
    lateinit var userStatusManager: UserStatusManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSikshaInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initOnClickListener()
    }

    private fun initView() {
        var currVersionNum = 0
        val latestVersionNum = args.latestVersionNum.toInt()
        BuildConfig.VERSION_NAME.split('.').forEachIndexed { idx, s ->
            currVersionNum += ((100.0F).pow(2 - idx) * (s.toIntOrNull() ?: 0)).toInt()
        }

        if (latestVersionNum == currVersionNum) {
            binding.versionCheckText.text = getString(R.string.setting_using_latest_version)
        } else {
            if (latestVersionNum != 0) binding.versionCheckText.text = getString(R.string.setting_need_update)
        }

        binding.version.text = BuildConfig.VERSION_NAME
    }

    private fun initOnClickListener() {
        with(binding) {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            withdrawalText.setOnClickListener {
                // TODO: SikshaDialogController 만들기
                DefaultDialog.newInstance(getString(R.string.siksha_info_dialog_withdrawal_content))
                    .show(childFragmentManager, "withdrawal dialog")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDialogNegativeClick() {}

    override fun onDialogPositiveClick() {
        lifecycleScope.launch {
            try {
                val withdrawCallback = { activity?.finish() }
                userStatusManager.deleteUser(requireContext(), withdrawCallback)
            } catch (e: IOException) {
                showToast(getString(R.string.common_network_error))
            }
        }
    }
}
