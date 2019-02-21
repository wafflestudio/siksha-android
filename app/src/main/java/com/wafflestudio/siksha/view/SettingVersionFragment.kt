package com.wafflestudio.siksha.view

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting_version, container, false)
        var version: String? = "Ver. "
        try {
            val packageInfo = context?.packageManager?.getPackageInfo(context?.packageName, 0)
            version += packageInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        view.text_siksha_version.text = version
        view.img_back.setOnClickListener {
            fragmentManager?.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        view.text_back.setOnClickListener {
            fragmentManager?.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return view
    }
}