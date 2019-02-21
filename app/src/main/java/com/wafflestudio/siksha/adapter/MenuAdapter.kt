package com.wafflestudio.siksha.adapter

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.model.Meal
import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.model.Restaurant
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuAdapter(
        getItems: () -> List<Menu>,
        private val infoButtonListener: (Restaurant) -> Unit,
        private val favoriteButtonListener: (Restaurant) -> Unit,
        private val onMealClickListener: (Meal, Restaurant) -> Unit
) : BaseAdapter<Menu, MenuAdapter.MenuHolder>(getItems) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_menu, parent, false)
        return MenuHolder(view, infoButtonListener, {
            favoriteButtonListener(it)
            refresh()
        }, onMealClickListener)
    }

    class MenuHolder(
            view: View,
            private val infoButtonListener: (Restaurant) -> Unit,
            private val favoriteButtonListener: (Restaurant) -> Unit,
            private val onMealClickListener: (Meal, Restaurant) -> Unit
    ) : BaseViewHolder<Menu>(view) {
        override fun bind(data: Menu) {
            view.text_restaurant_name.text = data.restaurant.krName
            view.list_meal.layoutManager = LinearLayoutManager(view.context)
            view.list_meal.adapter = MealAdapter({ data.meals }, data.restaurant, onMealClickListener)
            view.list_meal.isNestedScrollingEnabled = false
            if (data.restaurant.favorite) {
                view.button_favorite.setImageResource(R.drawable.restaurant_star_s)
            } else {
                view.button_favorite.setImageResource(R.drawable.restaurant_star_n)
            }
            view.button_info.setOnClickListener { infoButtonListener(data.restaurant) }
            view.button_favorite.setOnClickListener { favoriteButtonListener(data.restaurant) }
            view.text_open.setText(if (data.restaurant.isOpen) R.string.restaurant_open else R.string.restaurant_close)
            if (data.meals.isEmpty()) {
                view.text_no_menu.text = "식단이 없습니다"
            } else {
                view.text_no_menu.text = ""
            }
        }
    }
}