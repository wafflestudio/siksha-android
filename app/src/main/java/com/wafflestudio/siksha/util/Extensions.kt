package com.wafflestudio.siksha.util

import android.view.View

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

fun View.remove() {
    visibility = View.GONE
}