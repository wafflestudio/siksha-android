package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.preference.SikshaPreference
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_setting_version.view.*
import javax.inject.Inject


class SettingVersionFragment : Fragment() {

    @Inject
    lateinit var preference: SikshaPreference

    companion object {
        fun newInstance(): SettingVersionFragment = SettingVersionFragment()
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting_version, container, false)
        view.img_back.setOnClickListener { _ ->
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        view.text_back.setOnClickListener { _ ->
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return view
    }
}