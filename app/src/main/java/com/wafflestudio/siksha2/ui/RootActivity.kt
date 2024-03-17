package com.wafflestudio.siksha2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    @Inject
    lateinit var menuRepository: MenuRepository

    @Inject
    lateinit var restaurantRepository: RestaurantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        lifecycleScope.launch {
            menuRepository.sweepOldMenus()
            try {
                restaurantRepository.syncWithServer()
            } catch (e: IOException) {
                showToast(getString(R.string.common_network_error), Toast.LENGTH_SHORT)
            }
        }
    }
}
