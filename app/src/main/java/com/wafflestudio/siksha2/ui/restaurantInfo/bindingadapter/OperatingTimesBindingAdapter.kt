package com.wafflestudio.siksha2.ui.restaurantInfo.bindingadapter

import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.wafflestudio.siksha2.ui.restaurantInfo.model.DailyOperatingTimes
import com.wafflestudio.siksha2.ui.restaurantInfo.model.OperatingTime
import com.wafflestudio.siksha2.utils.setVisibleOrGone

@BindingAdapter("app:operatingTime")
fun Group.setOperatingTimeVisibility(operatingTime: OperatingTime?) {
    setVisibleOrGone(operatingTime != null)
}

@BindingAdapter("app:operatingTime")
fun TextView.setNoInfoVisibility(operatingTime: DailyOperatingTimes?) {
    setVisibleOrGone(operatingTime?.isEmpty() ?: true)
}
