package com.wafflestudio.siksha.view

import android.os.Bundle
import android.widget.Toast
import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import com.wafflestudio.siksha.util.compareDate
import dagger.android.AndroidInjection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
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
                    Toast.makeText(context, "식단을 가져오는데 실패했습니다", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            preference.menuResponse = it
                            preference.latestUpdate = SimpleDateFormat("MM. dd. HH:mm ").format(Date())
                            startActivity(MainActivity.createIntent(context, true))
                            finish()
                        }
                    } else Toast.makeText(context, "식단을 가져오는데 실패했습니다", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            startActivity(MainActivity.createIntent(context, false))
            finish()
        }
    }
}