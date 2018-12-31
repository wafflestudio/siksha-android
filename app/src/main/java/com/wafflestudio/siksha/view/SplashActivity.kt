package com.wafflestudio.siksha.view

import android.os.Bundle
import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import dagger.android.AndroidInjection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SplashActivity : BaseActivity() {
    @Inject
    lateinit var api: SikshaApi
    @Inject
    lateinit var preference: SikshaPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        api.fetchMenus().enqueue(object : Callback<MenuResponse> {
            override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                finish()
            }

            override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        preference.menuResponse = it
                        startActivity(MainActivity.createIntent(context))
                        finish()
                    }
                }
            }
        })
    }
}