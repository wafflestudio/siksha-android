package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.adapter.MenuAdapter
import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.model.MenuResponse
import com.wafflestudio.siksha.model.Review
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import com.wafflestudio.siksha.util.SikshaEncoder
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.bottom_restaurant_info.*
import kotlinx.android.synthetic.main.bottom_score.*
import kotlinx.android.synthetic.main.fragment_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

class MenuFragment : Fragment() {
    @Inject
    lateinit var api: SikshaApi
    @Inject
    lateinit var preference: SikshaPreference
    @Inject
    lateinit var encoder: SikshaEncoder

    var adapter: MenuAdapter? = null
    var infoSheet: BottomSheetDialog? = null
    var leaveScoreSheet: BottomSheetDialog? = null

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
        activity?.let { activity ->
            infoSheet = BottomSheetDialog(activity).apply {
                setContentView(activity.layoutInflater.inflate(R.layout.bottom_restaurant_info, null))
            }
            leaveScoreSheet = BottomSheetDialog(activity).apply {
                setContentView(activity.layoutInflater.inflate(R.layout.bottom_score, null))
            }
        }
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
                        infoSheet?.apply {
                            this.text_restaurant_name.text = restaurant.krName
                            text_restaurant_location.text = restaurant.location
                            text_restaurant_breakfast_operating_hours.text = restaurant.hoursBreakfast.replace('-', '~')
                            text_restaurant_lunch_operating_hours.text = restaurant.hoursLunch.replace('-', '~')
                            text_restaurant_dinner_operating_hours.text = restaurant.hoursDinner.replace('-', '~')
                            button_close.setOnClickListener { hide() }
                            show()
                        }
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
                    },
                    onMealClickListener = { meal, restaurant ->
                        if (isToday) {
                            leaveScoreSheet?.apply {
                                text_score_restaurant_name.text = restaurant.krName
                                text_meal_name.text = meal.krName
                                text_score.text = meal.score?.let {
                                    DecimalFormat("0.0").apply { roundingMode = RoundingMode.FLOOR }.format(it)
                                } ?: "-.-"
                                text_score_count.text = meal.scoreCount.toString()
                                button_score_close.setOnClickListener { hide() }
                                button_leave_score.setOnClickListener {
                                    Timber.d("rating ${rating_bar.rating}")

                                    val device = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
                                    val encoded = encoder.encode(meal.id, rating_bar.rating, device)

                                    api.leaveReview(encoded).enqueue(object : Callback<Review> {
                                        override fun onFailure(call: Call<Review>, t: Throwable) = Unit

                                        override fun onResponse(call: Call<Review>, response: Response<Review>) {
                                            if (response.isSuccessful) {
                                                api.fetchMenus().enqueue(object : Callback<MenuResponse> {
                                                    override fun onFailure(call: Call<MenuResponse>, t: Throwable) = Unit

                                                    override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                                                        if (response.isSuccessful) {
                                                            response.body()?.let { menuResponse -> preference.menuResponse = menuResponse }
                                                        }
                                                        this@MenuFragment.refresh()
                                                        leaveScoreSheet?.hide()
                                                    }
                                                })
                                            }
                                        }
                                    })
                                }
                                show()
                            }
                        }
                    }
            )
            list_menu.adapter = adapter
        }
    }
}