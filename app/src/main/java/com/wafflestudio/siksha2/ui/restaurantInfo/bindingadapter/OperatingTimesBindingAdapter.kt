package com.wafflestudio.siksha2.ui.restaurantInfo.bindingadapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.wafflestudio.siksha2.ui.restaurantInfo.model.DailyOperatingTimes
import com.wafflestudio.siksha2.ui.restaurantInfo.model.OperatingTime
import com.wafflestudio.siksha2.utils.setVisibleOrGone

@BindingAdapter("app:dailyOperatingTimes")
fun ConstraintLayout.setDailyOperatingTimeVisibility(dailyOperatingTimes: DailyOperatingTimes?) {
    setVisibleOrGone(dailyOperatingTimes != null)
}

@BindingAdapter("app:dailyOperatingTimes")
fun View.setDailyOperatingTimeDividerVisibility(dailyOperatingTimes: DailyOperatingTimes?) {
    setVisibleOrGone(dailyOperatingTimes != null)
}

@BindingAdapter("app:operatingTime")
fun Group.setOperatingTimeVisibility(operatingTime: OperatingTime?) {
    setVisibleOrGone(operatingTime != null)
}
