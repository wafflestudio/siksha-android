package com.wafflestudio.siksha.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    companion object {
        fun createIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    @Inject
    lateinit var api: SikshaApi
    @Inject
    lateinit var preference: SikshaPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initEvents()
    }

    private fun initViews() {
        view_pager.adapter = MainPagerAdapter(supportFragmentManager)
        view_pager.setCurrentItem(1, false)
    }

    private fun initEvents() {
        tab_layout_navigation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    MainPagerAdapter.FAVORITE_INDEX -> {
                        include.setBackgroundColor(color(android.R.color.transparent))
                    }
                    MainPagerAdapter.MENU_INDEX -> {
                        include.setBackgroundColor(color(R.color.white))
                    }
                    MainPagerAdapter.SETTING_INDEX -> {
                        include.setBackgroundColor(color(android.R.color.transparent))
                    }
                }
                view_pager.setCurrentItem(tab.position, false)
            }
        })
        tab_layout_navigation.getTabAt(MainPagerAdapter.MENU_INDEX)?.select()
    }
}
