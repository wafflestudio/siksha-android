package com.wafflestudio.siksha.view.activity.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import com.wafflestudio.siksha.view.BaseActivity
import com.wafflestudio.siksha.view.activity.favourite.FavoriteFragment
import com.wafflestudio.siksha.view.adapter.MainPagerAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity(), HasSupportFragmentInjector {

  @Inject
  lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

  companion object {
    private const val EXTRA_WAS_UPDATED = "MAIN_WAS_UPDATED"

    fun createIntent(context: Context, wasUpdated: Boolean): Intent? =
        Intent(context, MainActivity::class.java).putExtra(EXTRA_WAS_UPDATED, wasUpdated)
  }

  private val wasUpdated by lazy { intent.getBooleanExtra(EXTRA_WAS_UPDATED, false) }

  @Inject
  lateinit var api: SikshaApi
  @Inject
  lateinit var preference: SikshaPreference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (!wasUpdated) {
      Timber.d("Updating menus in main activity")
      api.fetchMenus().enqueue(object : Callback<MenuResponse> {
        override fun onFailure(call: Call<MenuResponse>, t: Throwable) = Unit
        override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
          if (response.isSuccessful) {
            response.body()?.let {
              preference.menuResponse = it
            }
          }
        }
      })
    }

    initPager()
  }

  private fun initPager() {
    val adapter = MainPagerAdapter(supportFragmentManager)
    view_pager.adapter = adapter
    view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) = Unit
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
      override fun onPageSelected(position: Int) {
        when (position) {
          MainPagerAdapter.FAVORITE_INDEX -> (adapter.getItem(position) as? FavoriteFragment)?.refresh()
          MainPagerAdapter.MAIN_INDEX -> (adapter.getItem(position) as? MainFragment)?.refresh()
        }
      }
    })
    tab_layout_navigation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabReselected(tab: TabLayout.Tab) = Unit
      override fun onTabUnselected(tab: TabLayout.Tab) = Unit
      override fun onTabSelected(tab: TabLayout.Tab) {
        when (tab.position) {
          MainPagerAdapter.FAVORITE_INDEX -> {
            if (preference.favorite.isEmpty()) {
              include.setBackgroundColor(color(android.R.color.transparent))
            } else {
              include.setBackgroundColor(color(R.color.white))
            }
          }
          MainPagerAdapter.MAIN_INDEX -> {
            include.setBackgroundColor(color(R.color.white))
          }
          MainPagerAdapter.SETTING_INDEX -> {
            include.setBackgroundColor(color(android.R.color.transparent))
          }
        }
        view_pager.setCurrentItem(tab.position, false)
      }
    })
    tab_layout_navigation.getTabAt(MainPagerAdapter.MAIN_INDEX)?.select()
  }
}
