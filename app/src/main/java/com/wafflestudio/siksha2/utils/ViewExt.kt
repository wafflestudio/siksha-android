package com.wafflestudio.siksha2.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.getInflater(): LayoutInflater {
    return LayoutInflater.from(context)
}

fun Context.dp(dp: Int): Int {
    return dp * resources.displayMetrics.density.toInt()
}

fun Context.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.requireContext(), message, duration).show()
}

fun ImageView.setImageUrl(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}
