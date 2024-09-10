package com.wafflestudio.siksha2.ui.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentSettingBinding
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val vm: SettingViewModel by activityViewModels()

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

        binding.versionText.text = getString(R.string.version_text, vm.packageVersion)

        vm.userData.observe(viewLifecycleOwner) { user ->
            binding.nickname.text = user.nickname

            val imageUrl = user.profileUrl
            if (imageUrl != null) {
                Glide.with(this).load(imageUrl).circleCrop().into(binding.ivProfilePicture)
            } else {
                binding.ivProfilePicture.apply {
                    setImageResource(R.drawable.ic_rice_bowl)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }

        if (vm.versionCheck.value == true) {
            binding.versionCheckText.text = getString(R.string.setting_using_latest_version)
        } else {
            binding.versionCheckText.text = getString(R.string.setting_need_update)
        }

        binding.infoRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToUserAccountFragment()
            findNavController().navigate(action)
        }

        binding.myPostRow.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainFragmentToUserPostListFragment()
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
                MainFragmentDirections.actionMainFragmentToSettingAccountFragment()
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
