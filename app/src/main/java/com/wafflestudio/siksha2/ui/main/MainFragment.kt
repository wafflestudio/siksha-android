package com.wafflestudio.siksha2.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var stateAdapter: FragmentStateAdapter

    private val vm: MainViewModel by activityViewModels()

    private var currentTabState = MainTabState.MAIN

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTab()
    }

    private fun initTab() {
        stateAdapter = MainFragmentStateAdapter(this)
        binding.viewPager.apply {
            adapter = stateAdapter
            isUserInputEnabled = false
            setCurrentItem(currentTabState.ordinal, false)
        }

        binding.tabLayout.addTab(
            binding.tabLayout.newTab()
                .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_community, null)),
            2,
            false
        )

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab: TabLayout.Tab, i: Int ->
            tab.icon = when (i) {
                MainTabState.FAVORITE.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_favorite, null)
                MainTabState.MAIN.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_main, null)
                MainTabState.COMMUNITY.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_community, null)
                MainTabState.SETTINGS.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_setting, null)
                else -> throw IllegalStateException("no such tab with index $i")
            }
        }.attach()
    }

    override fun onStop() {
        super.onStop()
        vm.setVpState(binding.viewPager.currentItem)
        currentTabState = MainTabState.fromPosition(binding.viewPager.currentItem)
    }
}
