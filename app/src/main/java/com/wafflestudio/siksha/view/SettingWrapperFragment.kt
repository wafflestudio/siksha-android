package com.wafflestudio.siksha.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R

class SettingWrapperFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance(): SettingWrapperFragment = SettingWrapperFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting_wrapper, container, false)
        fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_setting_view, SettingFragment.newInstance())
                ?.commit()
        return view
    }
}