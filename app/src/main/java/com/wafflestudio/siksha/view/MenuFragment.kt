package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.adapter.MenuAdapter
import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.preference.SikshaPreference
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_menu.*
import javax.inject.Inject

class MenuFragment : Fragment() {
    @Inject
    lateinit var preference: SikshaPreference

    var adapter: MenuAdapter? = null

    companion object {
        const val EXTRA_IS_TODAY = "MENU_FRAGMENT_IS_TODAY"
        const val EXTRA_TYPE = "MENU_FRAGMENT_TYPE"
        const val EXTRA_ONLY_FAVORITES = "MENU_ONLY_FAVORITES"

        fun newInstance(isToday: Boolean, menuType: Menu.Type, onlyFavorites: Boolean) = MenuFragment().apply {
            arguments = Bundle().apply {
                putBoolean(EXTRA_IS_TODAY, isToday)
                putInt(EXTRA_TYPE, menuType.ordinal)
                putBoolean(EXTRA_ONLY_FAVORITES, onlyFavorites)
            }
        }
    }

    private val isToday: Boolean by lazy { arguments?.getBoolean(EXTRA_IS_TODAY) ?: true }
    private val menuType: Menu.Type by lazy {
        Menu.Type.values()[arguments?.getInt(EXTRA_TYPE) ?: 0]
    }
    private val onlyFavorites: Boolean by lazy {
        arguments?.getBoolean(EXTRA_ONLY_FAVORITES) ?: false
    }

    fun refresh() {
        adapter?.refresh()
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    private fun initViews() {
        preference.menuResponse?.let { menuResponse ->
            val getMenus = {
                (if (isToday) menuResponse.today else menuResponse.tomorrow).menus
                        .filter { it.type == menuType }
                        .filter { !onlyFavorites || preference.favorite.contains(it.restaurant.code) }
                        .map {
                            it.copy(
                                    restaurant = it.restaurant.copy(
                                            favorite = preference.favorite.contains(it.restaurant.code)
                                    )
                            )
                        }
            }
            list_menu.layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(getMenus,
                    infoButtonListener = { restaurant ->
                    },
                    favoriteButtonListener = { restaurant ->
                        if (restaurant.favorite) {
                            preference.removeFavorite(restaurant.code)
                            if (preference.favorite.isEmpty()) {
                                (parentFragment as? FavoriteFragment)?.refresh()
                            }
                        } else {
                            preference.addFavorite(restaurant.code)
                        }
                    }
            )
            list_menu.adapter = adapter
        }
    }
}