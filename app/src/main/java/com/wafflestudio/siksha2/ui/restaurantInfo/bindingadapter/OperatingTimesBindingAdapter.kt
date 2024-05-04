package com.wafflestudio.siksha2.ui.restaurantInfo.bindingadapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.wafflestudio.siksha2.ui.restaurantInfo.model.DailyOperatingTimes
import com.wafflestudio.siksha2.ui.restaurantInfo.model.OperatingTime
import com.wafflestudio.siksha2.utils.setVisibleOrGone

@BindingAdapter("app:dailyOperatingTimes")
fun ConstraintLayout.setDailyOperatingTimeVisibility(dailyOperatingTimes: DailyOperatingTimes?) {
    if (dailyOperatingTimes == null) {
        setVisibleOrGone(false)
        return
    }

    setVisibleOrGone(true)
}

@BindingAdapter("app:operatingTime")
fun Group.setOperatingTimeVisibility(operatingTime: OperatingTime?) {
    if (operatingTime == null) {
        setVisibleOrGone(false)
        return
    }

    setVisibleOrGone(true)
}
