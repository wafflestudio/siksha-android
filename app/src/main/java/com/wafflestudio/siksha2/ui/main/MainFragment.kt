package com.wafflestudio.siksha2.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var stateAdapter: MainFragmentStateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateAdapter = MainFragmentStateAdapter(this)
        binding.viewPager.apply {
            adapter = stateAdapter
            isUserInputEnabled = false
            setCurrentItem(1, false)
        }

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab: TabLayout.Tab, i: Int ->
            tab.icon = when (i) {
                0 -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_favorite, null)
                1 -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_main, null)
                2 -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tab_setting, null)
                else -> throw IllegalStateException("no such tab with index $i")
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
}
