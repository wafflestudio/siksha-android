package com.wafflestudio.siksha2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
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

        val navHost = findViewById<FragmentContainerView>(R.id.nav_host)
        ViewCompat.setOnApplyWindowInsetsListener(navHost) { v, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = insets.left,
                top = insets.top,
                bottom = insets.bottom,
                right = insets.right
            )
            WindowInsetsCompat.CONSUMED
        }

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
