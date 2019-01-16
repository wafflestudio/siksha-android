package com.wafflestudio.siksha.view

import android.os.Bundle
import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import com.wafflestudio.siksha.util.compareDate
import dagger.android.AndroidInjection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class SplashActivity : BaseActivity() {
    @Inject
    lateinit var api: SikshaApi
    @Inject
    lateinit var preference: SikshaPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val needsUpdate = preference.menuResponse?.let { !compareDate(it.today.date) } ?: true

        if (needsUpdate) {
            Timber.d("Updating menus in splash activity")
            api.fetchMenus().enqueue(object : Callback<MenuResponse> {
                override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                    Timber.d("failed to fetch datas: "+t.message)
                    finish()
                }

                override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            preference.menuResponse = it
                            startActivity(MainActivity.createIntent(context, true))
                            finish()
                        }
                    }
                }
            })
        } else {
            startActivity(MainActivity.createIntent(context, false))
            finish()
        }
    }
}