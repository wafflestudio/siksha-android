package com.wafflestudio.siksha.view.activity.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.preference.SikshaPreference
import com.wafflestudio.siksha.util.compareDate
import com.wafflestudio.siksha.util.formatDate
import com.wafflestudio.siksha.util.getCurrentType
import com.wafflestudio.siksha.view.activity.menu.MenuFragment
import com.wafflestudio.siksha.view.adapter.MenuPagerAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

open class MainFragment : Fragment(), HasSupportFragmentInjector {

  @Inject
  lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

  open val onlyFavorites = false

  private var selectedDayIsTomorrow = false
  private var selectedType: Menu.Type = Menu.Type.BREAKFAST
  private lateinit var onTabSelectedListener: TabLayout.OnTabSelectedListener
  var adapter: MenuPagerAdapter? = null

  @Inject
  lateinit var preference: SikshaPreference

  companion object {
    fun newInstance(): MainFragment = MainFragment()
  }

  open fun refresh() {
    listOf(
        MenuPagerAdapter.TODAY_BREAKFAST_INDEX,
        MenuPagerAdapter.TODAY_LUNCH_INDEX,
        MenuPagerAdapter.TODAY_DINNER_INDEX,
        MenuPagerAdapter.TOMORROW_BREAKFAST_INDEX,
        MenuPagerAdapter.TOMORROW_LUNCH_INDEX,
        MenuPagerAdapter.TOMORROW_DINNER_INDEX
    ).map { position ->
      (adapter?.getItem(position) as? MenuFragment)?.refresh()
    }
  }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
      inflater.inflate(R.layout.fragment_main, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initPager()
    initEvents()

    selectedDayIsTomorrow = compareDate(preference.menuResponse?.tomorrow?.date ?: "")
    selectedType = getCurrentType()
    var index = when (selectedType) {
      Menu.Type.BREAKFAST -> 0
      Menu.Type.LUNCH -> 1
      Menu.Type.DINNER -> 2
    }
    if (selectedDayIsTomorrow) index += 3
    view_pager.setCurrentItem(index, false)
    when (index) {
      0, 3 -> tab_item_breakfast.setImageResource(R.drawable.breakfast_s)
      1, 4 -> tab_item_lunch.setImageResource(R.drawable.lunch_s)
      2, 5 -> tab_item_dinner.setImageResource(R.drawable.dinner_s)
    }
  }

  private fun initPager() {
    listOf(
        preference.menuResponse?.today?.date ?: getString(R.string.today),
        preference.menuResponse?.tomorrow?.date ?: getString(R.string.tomorrow)
    ).map { formatDate(it) }.forEach {
      tab_layout_date.addTab(tab_layout_date.newTab().setText(it))
    }
    adapter = MenuPagerAdapter(childFragmentManager, onlyFavorites)
    view_pager.adapter = adapter
    view_pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) = Unit
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
      override fun onPageSelected(position: Int) {
        setTabSelected(position)
        (adapter?.getItem(position) as? MenuFragment)?.refresh()
      }
    })
  }

  private fun initEvents() {
    val tabItems = listOf(tab_item_breakfast, tab_item_lunch, tab_item_dinner)
    tabItems.map { tab ->
      tab.setOnClickListener {
        var index = when (tab.id) {
          tab_item_breakfast.id -> 0
          tab_item_lunch.id -> 1
          tab_item_dinner.id -> 2
          else -> throw Exception("No tab with that id")
        }
        if (selectedDayIsTomorrow) index += 3
        view_pager.setCurrentItem(index, true)
      }
    }
    onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
      override fun onTabReselected(tab: TabLayout.Tab?) = Unit
      override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
      override fun onTabSelected(tab: TabLayout.Tab?) {
        var index = selectedType.ordinal
        selectedDayIsTomorrow = tab?.position == MenuPagerAdapter.TOMORROW_INDEX
        if (selectedDayIsTomorrow) index += 3
        view_pager.setCurrentItem(index, true)
      }
    }
    tab_layout_date.addOnTabSelectedListener(onTabSelectedListener)
  }

  // only use in view pager listener
  private fun setTabSelected(position: Int) {
    tab_item_breakfast.setImageResource(R.drawable.breakfast)
    tab_item_lunch.setImageResource(R.drawable.lunch)
    tab_item_dinner.setImageResource(R.drawable.dinner)
    when (position) {
      MenuPagerAdapter.TODAY_BREAKFAST_INDEX,
      MenuPagerAdapter.TOMORROW_BREAKFAST_INDEX -> {
        selectedType = Menu.Type.BREAKFAST
        tab_item_breakfast.setImageResource(R.drawable.breakfast_s)
      }
      MenuPagerAdapter.TODAY_LUNCH_INDEX,
      MenuPagerAdapter.TOMORROW_LUNCH_INDEX -> {
        selectedType = Menu.Type.LUNCH
        tab_item_lunch.setImageResource(R.drawable.lunch_s)
      }
      MenuPagerAdapter.TODAY_DINNER_INDEX,
      MenuPagerAdapter.TOMORROW_DINNER_INDEX -> {
        selectedType = Menu.Type.DINNER
        tab_item_dinner.setImageResource(R.drawable.dinner_s)
      }
    }
    when (position) {
      MenuPagerAdapter.TODAY_BREAKFAST_INDEX,
      MenuPagerAdapter.TODAY_LUNCH_INDEX,
      MenuPagerAdapter.TODAY_DINNER_INDEX -> {
        selectedDayIsTomorrow = false
        tab_layout_date.getTabAt(MenuPagerAdapter.TODAY_INDEX)?.let {
          tab_layout_date.removeOnTabSelectedListener(onTabSelectedListener)
          it.select()
          tab_layout_date.addOnTabSelectedListener(onTabSelectedListener)
        }
      }
      MenuPagerAdapter.TOMORROW_BREAKFAST_INDEX,
      MenuPagerAdapter.TOMORROW_LUNCH_INDEX,
      MenuPagerAdapter.TOMORROW_DINNER_INDEX -> {
        selectedDayIsTomorrow = true
        tab_layout_date.getTabAt(MenuPagerAdapter.TOMORROW_INDEX)?.let {
          tab_layout_date.removeOnTabSelectedListener(onTabSelectedListener)
          it.select()
          tab_layout_date.addOnTabSelectedListener(onTabSelectedListener)
        }
      }
    }
  }
}