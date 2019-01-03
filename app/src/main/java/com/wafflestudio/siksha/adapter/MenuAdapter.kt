package com.wafflestudio.siksha.adapter

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.model.Menu
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuAdapter(items: List<Menu>) : BaseAdapter<Menu, MenuAdapter.MenuHolder>(items) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_menu, parent, false)
        return MenuHolder(view)
    }

    class MenuHolder(view: View) : BaseViewHolder<Menu>(view) {
        override fun bind(data: Menu) {
            view.text_restaurant_name.text = data.restaurant.krName
            view.list_meal.layoutManager = LinearLayoutManager(view.context)
            view.list_meal.adapter = MealAdapter(data.meals)
            view.list_meal.isNestedScrollingEnabled = false
        }
    }
}