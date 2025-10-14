package com.wafflestudio.siksha2.components

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.wafflestudio.siksha2.R

class ToggleSwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val bgActive: ImageView
    private val bgInactive: ImageView
    private val knob: ImageView

    var isActive = false
        private set

    init {
        LayoutInflater.from(context).inflate(R.layout.item_toggle_switch, this, true)
        bgActive = findViewById(R.id.bg_active)
        bgInactive = findViewById(R.id.bg_inactive)
        knob = findViewById(R.id.toggle_knob)

        post {
            val minTouchSize = (48 * resources.displayMetrics.density).toInt()
            val parent = this.parent as? View ?: return@post
            parent.post {
                val rect = Rect()
                getHitRect(rect)
                val widthDiff = (minTouchSize - rect.width()).coerceAtLeast(0) / 2
                val heightDiff = (minTouchSize - rect.height()).coerceAtLeast(0) / 2
                rect.inset(-widthDiff, -heightDiff)
                parent.touchDelegate = TouchDelegate(rect, this)
            }
        }

        setOnClickListener { toggle() }
        updateAppearance(animated = false)
    }

    fun toggle() {
        isActive = !isActive
        updateAppearance(animated = true)
    }

    fun setActive(active: Boolean, animated: Boolean = false) {
        if (isActive != active) {
            isActive = active
            updateAppearance(animated)
        }
    }

    private fun updateAppearance(animated: Boolean) {
        bgActive.visibility = if (isActive) VISIBLE else GONE
        bgInactive.visibility = if (isActive) GONE else VISIBLE

        post {
            val knobMargin = 3f
            val maxTravel = width - knob.width - knobMargin * 2
            val targetX = if (isActive) knobMargin + maxTravel else knobMargin

            if (animated) {
                knob.animate()
                    .x(targetX)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setDuration(150)
                    .start()
            } else {
                knob.x = targetX
            }
        }
    }
}
