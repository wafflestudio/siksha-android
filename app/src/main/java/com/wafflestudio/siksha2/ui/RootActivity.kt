package com.wafflestudio.siksha2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import com.wafflestudio.siksha2.utils.applyTopMarginForStatusBarSpacing
import com.wafflestudio.siksha2.utils.applyStatusBarSpacing
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_root)

        val navHost = findViewById<FragmentContainerView>(R.id.nav_host)
        ViewCompat.setOnApplyWindowInsetsListener(navHost) { v, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = insets.left,
                bottom = insets.bottom,
                right = insets.right
            )
            windowInsets
        }
        ViewCompat.requestApplyInsets(navHost)
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: androidx.fragment.app.FragmentManager,
                    f: androidx.fragment.app.Fragment,
                    v: android.view.View,
                    savedInstanceState: Bundle?
                ) {
                    v.findViewById<android.view.View?>(R.id.top_bar)?.applyStatusBarSpacing(extraTopDp = 0)
                    v.findViewById<android.view.View?>(R.id.tool_bar)?.applyStatusBarSpacing(extraTopDp = 0)
                    v.findViewById<android.view.View?>(R.id.main_logo)?.applyTopMarginForStatusBarSpacing(extraTopDp = 0)
                }
            },
            true
        )

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
