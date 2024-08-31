package com.wafflestudio.siksha2.ui.main.setting.voc

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentVocBinding
import com.wafflestudio.siksha2.network.dto.VocParam
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class VocFragment : Fragment() {

    private lateinit var binding: FragmentVocBinding

    @Inject
    lateinit var userStatusManager: UserStatusManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVocBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            try {
                val userData = userStatusManager.getUserData()
                binding.idText.text = "ID " + userData.id
            } catch (e: IOException) {
                showToast("네트워크 연결이 불안정합니다.")
            }
        }
        binding.commentEdit.filters = binding.commentEdit.filters + InputFilter.LengthFilter(500)
        binding.textCount.text = getString(
            R.string.leave_review_text_count,
            0,
            500
        )

        binding.commentEdit.addTextChangedListener {
            binding.textCount.text = getString(
                R.string.leave_review_text_count,
                it?.length,
                500
            )
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    userStatusManager.sendVoc(voc=binding.commentEdit.text.toString(),platform = "Android")
                    showToast("문의가 정상적으로 등록되었습니다.")
                    findNavController().popBackStack()
                } catch (e: IOException) {
                    Timber.e(e)
                    showToast("네트워크 연결이 불안정합니다.")
                }
            }
        }
    }
}
