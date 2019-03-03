package com.wafflestudio.siksha.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.adapter.MenuAdapter
import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.model.Review
import com.wafflestudio.siksha.network.SikshaApi
import com.wafflestudio.siksha.preference.SikshaPreference
import com.wafflestudio.siksha.util.SikshaEncoder
import com.wafflestudio.siksha.util.visible
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.bottom_restaurant_info.*
import kotlinx.android.synthetic.main.bottom_score.*
import kotlinx.android.synthetic.main.fragment_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
                        .sortedWith(object : Comparator<Menu> {
                            override fun compare(p0: Menu, p1: Menu) =
                                    if (onlyFavorites) preference.getFavoriteRestaurantPriority(p0.restaurant.code) -
                                            preference.getFavoriteRestaurantPriority(p1.restaurant.code)
                                    else
                                        preference.getRestaurantPriority(p0.restaurant.code) -
                                                preference.getRestaurantPriority(p1.restaurant.code)
                        })
                        .filter { preference.visibleNoMenu || it.meals.isNotEmpty() }
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
                            button_google_map.setOnClickListener {
                                val googleUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${restaurant.latitude},${restaurant.longitude}")
                                val googleMapIntent = Intent(Intent.ACTION_VIEW, googleUri)
                                startActivity(googleMapIntent)
                            }
                            button_kakao_map.setOnClickListener {
                                val kakaoUri = Uri.parse("daummaps://look?p=${restaurant.latitude},${restaurant.longitude}")
                                val kakaoMapIntent = Intent(Intent.ACTION_VIEW, kakaoUri)
                                startActivity(kakaoMapIntent)
                            }
                            button_naver_map.setOnClickListener {
                                val naverUri = Uri.parse("nmap://place?lat=${restaurant.latitude}&lng=${restaurant.longitude}&appname=com.wafflestudio.siksha")
                                val naverMapIntent = Intent(Intent.ACTION_VIEW, naverUri)
                                startActivity(naverMapIntent)
                            }
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
                        leaveScoreSheet?.apply {
                            text_score_restaurant_name.text = restaurant.krName
                            text_meal_name.text = meal.krName
                            text_score.text = meal.score?.let {
                                DecimalFormat("0.0").apply { roundingMode = RoundingMode.FLOOR }.format(it)
                            } ?: "-.-"
                            text_score_count.text = meal.scoreCount.toString()
                            button_score_close.setOnClickListener { hide() }
                            val scorable = isToday && when (menuType) {
                                Menu.Type.BREAKFAST -> !preference.reviewedBreakfast
                                Menu.Type.LUNCH -> !preference.reviewedLunch
                                Menu.Type.DINNER -> !preference.reviewedDinner
                            }
                            button_leave_score.isEnabled = scorable
                            if(!scorable){
                                if(!isToday){
                                    button_leave_score.text = "오늘 메뉴만 평가 가능합니다."
                                }
                                else{
                                    button_leave_score.text = "해당 시간대에 이미 평가하셨습니다."
                                }
                            }
                            rating_bar.visible = scorable
                            button_leave_score.setOnClickListener {
                                val device = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
                                val encoded = encoder.encode(meal.id, rating_bar.rating, device)

                                api.leaveReview(encoded).enqueue(object : Callback<Review> {
                                    override fun onFailure(call: Call<Review>, t: Throwable) = Unit

                                    override fun onResponse(call: Call<Review>, response: Response<Review>) {
                                        if (response.isSuccessful) {
                                            response.body()?.let { review ->
                                                preference.registerReview(meal, menuType, review)
                                                hide()
                                                refresh()
                                            }
                                        }
                                    }
                                })
                            }
                            show()
                        }
                    }
            )
            list_menu.adapter = adapter
        }
    }
}