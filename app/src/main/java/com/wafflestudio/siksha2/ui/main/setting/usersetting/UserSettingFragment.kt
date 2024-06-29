package com.wafflestudio.siksha2.ui.main.setting.usersetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.wafflestudio.siksha2.databinding.FragmentSettingUsersettingBinding
import com.wafflestudio.siksha2.repositories.UserStatusManager

class UserSettingFragment : Fragment() {
    private lateinit var binding:FragmentSettingUsersettingBinding

    lateinit var userStatusManager: UserStatusManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            binding = FragmentSettingUsersettingBinding.inflate(inflater, container, false)
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
