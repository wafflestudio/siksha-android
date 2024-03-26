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
import com.wafflestudio.siksha2.ui.SikshaDialog
import com.wafflestudio.siksha2.ui.SikshaDialogListener
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject
import kotlin.math.pow

@AndroidEntryPoint
class SikshaInfoFragment : Fragment() {
    private lateinit var binding: FragmentSikshaInfoBinding
    private val args: SikshaInfoFragmentArgs by navArgs()

    @Inject
    lateinit var userStatusManager: UserStatusManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSikshaInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.withdrawalText.setOnClickListener {
            // TODO: SikshaDialogController 만들기
            val dialog = SikshaDialog.newInstance("앱 계정을 삭제합니다.\n이 계정으로 등록된 리뷰 정보들도 모두 함께 삭제됩니다.")
            dialog.setListener(
                object : SikshaDialogListener {
                    override fun onPositive() {
                        lifecycleScope.launch {
                            try {
                                val withdrawCallback = { activity?.finish() }
                                userStatusManager.deleteUser(requireContext(), withdrawCallback)
                            } catch (e: IOException) {
                                showToast(getString(R.string.common_network_error))
                            }
                        }
                    }

                    override fun onNegative() {
                        dialog.dismiss()
                    }
                }
            )
            dialog.show(childFragmentManager, "withdrawal")
        }
    }
}
