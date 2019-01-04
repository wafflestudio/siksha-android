package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.preference.SikshaPreference
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_menu.*
import javax.inject.Inject

class MenuFragment : Fragment() {
    @Inject
    lateinit var preference: SikshaPreference

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initEvents()
    }

    private fun initViews() {
        listOf(
                preference.menuResponse?.today?.date ?: getString(R.string.today),
                preference.menuResponse?.tomorrow?.date ?: getString(R.string.tomorrow)
        ).map {
            tab_layout_date.addTab(tab_layout_date.newTab().setText(it))
        }
    }

    private fun initEvents() {
        val tabItems = listOf(tab_item_breakfast, tab_item_lunch, tab_item_dinner)
        tabItems.map { tab ->
            tab.setOnClickListener {
                tab_item_breakfast.setImageResource(R.drawable.breakfast)
                tab_item_lunch.setImageResource(R.drawable.lunch)
                tab_item_dinner.setImageResource(R.drawable.dinner)
                when (it.id) {
                    tab_item_breakfast.id -> tab_item_breakfast.setImageResource(R.drawable.breakfast_s)
                    tab_item_lunch.id -> tab_item_lunch.setImageResource(R.drawable.lunch_s)
                    tab_item_dinner.id -> tab_item_dinner.setImageResource(R.drawable.dinner_s)
                }
            }
        }
    }

    companion object {
        fun newInstance(): MenuFragment = MenuFragment()
    }
}