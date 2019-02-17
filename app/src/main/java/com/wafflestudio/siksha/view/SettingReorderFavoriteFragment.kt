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
import javax.inject.Inject

class SettingReorderFavoriteFragment : Fragment() {

    @Inject
    lateinit var preference: SikshaPreference

    companion object {
        fun newInstance(): SettingReorderFavoriteFragment = SettingReorderFavoriteFragment()
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting_reorder, container, false)
        return view
    }
}