package com.wafflestudio.siksha.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.model.Meal
import com.wafflestudio.siksha.model.Restaurant
import kotlinx.android.synthetic.main.item_meal.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class MealAdapter(
        getItems: () -> List<Meal>,
        val restaurant: Restaurant,
        private val onMealClickListener: (Meal, Restaurant) -> Unit
) : BaseAdapter<Meal, MealAdapter.MealHolder>(getItems) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_meal, parent, false)
        return MealHolder(view, restaurant, onMealClickListener)
    }

    class MealHolder(
            view: View,
            private val restaurant: Restaurant,
            private val onMealClickListener: (Meal, Restaurant) -> Unit
    ) : BaseViewHolder<Meal>(view) {
        override fun bind(data: Meal) {
            view.text_price.text = data.price
            view.text_meal.text = data.krName
            view.text_score.text = data.score?.let {
                DecimalFormat("0.0").apply { roundingMode = RoundingMode.FLOOR }.format(it)
            } ?: "-.-"
            view.layout.setOnClickListener { onMealClickListener(data, restaurant) }
        }
    }
}