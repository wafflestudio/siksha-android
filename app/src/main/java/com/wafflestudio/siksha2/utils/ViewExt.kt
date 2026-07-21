package com.wafflestudio.siksha2.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.wafflestudio.siksha2.ui.common.ImageViewerActivity
import kotlin.math.roundToInt

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

fun Context.showImageViewer(images: List<String>, initialPage: Int) {
    startActivity(ImageViewerActivity.createIntent(this, images, initialPage))
}

fun View.applyStatusBarSpacing(extraTopDp: Int = 0) {
    val initialHeight = layoutParams.height
    val initialPaddingTop = paddingTop
    val extraTopPx = extraTopDp
        .times(resources.displayMetrics.density)
        .roundToInt()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val topInset = windowInsets.getInsets(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
        ).top

        if (initialHeight > 0) {
            view.updateLayoutParams<ViewGroup.LayoutParams> {
                height = initialHeight + topInset + extraTopPx
            }
        }
        view.updatePadding(top = initialPaddingTop + topInset + extraTopPx)

        windowInsets
    }

    ViewCompat.requestApplyInsets(this)
}

fun View.applyTopMarginForStatusBarSpacing(extraTopDp: Int = 0) {
    val initialTopMargin = (layoutParams as? MarginLayoutParams)?.topMargin ?: 0
    val extraTopPx = extraTopDp
        .times(resources.displayMetrics.density)
        .roundToInt()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val topInset = windowInsets.getInsets(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
        ).top

        view.updateLayoutParams<MarginLayoutParams> {
            topMargin = initialTopMargin + topInset + extraTopPx
        }

        windowInsets
    }

    ViewCompat.requestApplyInsets(this)
}
