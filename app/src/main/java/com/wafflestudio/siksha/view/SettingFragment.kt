package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.preference.SikshaPreference
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_setting.view.*
import javax.inject.Inject

class SettingFragment : Fragment() {

    @Inject
    lateinit var preference: SikshaPreference

    companion object {
        fun newInstance(): SettingFragment = SettingFragment()
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        view.text_siksha_information.setOnClickListener { _ ->
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_setting_view, SettingVersionFragment.newInstance())
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    ?.addToBackStack(null)
                    ?.commit()
        }
        view.img_new.visibility = View.INVISIBLE
        view.text_reorder.setOnClickListener { _->
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_setting_view, SettingReorderMainFragment.newInstance())
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    ?.addToBackStack(null)
                    ?.commit()
        }
        view.text_reorder_favorite.setOnClickListener { _->
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_setting_view, SettingReorderFavoriteFragment.newInstance())
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    ?.addToBackStack(null)
                    ?.commit()
        }
        return view
    }
}