package com.wafflestudio.siksha2.ui.main.setting.reorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.R

class RestaurantListCardDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val density = context.resources.displayMetrics.density
    private val horizontalInset = 16f * density
    private val verticalInset = (20f * density).toInt()
    private val cornerRadius = 12f * density
    private val shadowRadius = 3f * density

    private val cardPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.background_main)
            setShadowLayer(shadowRadius, 0f, density, Color.argb(48, 0, 0, 0))
        }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        outRect.left = horizontalInset.toInt()
        outRect.right = horizontalInset.toInt()
        if (position == 0) outRect.top = verticalInset
        if (position == state.itemCount - 1) outRect.bottom = verticalInset
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (state.itemCount == 0 || parent.childCount == 0) return

        var firstView: View? = null
        var lastView: View? = null
        var firstPosition = Int.MAX_VALUE
        var lastPosition = Int.MIN_VALUE

        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) continue

            if (position < firstPosition) {
                firstPosition = position
                firstView = child
            }
            if (position > lastPosition) {
                lastPosition = position
                lastView = child
            }
        }

        val top =
            if (firstPosition == 0) {
                firstView?.top?.toFloat() ?: return
            } else {
                -cornerRadius
            }
        val bottom =
            if (lastPosition == state.itemCount - 1) {
                lastView?.bottom?.toFloat() ?: return
            } else {
                parent.height + cornerRadius
            }
        val horizontalReference = firstView ?: lastView ?: return
        val left = horizontalReference.left.toFloat()
        val right = horizontalReference.right.toFloat()

        if (parent.layerType != View.LAYER_TYPE_SOFTWARE) {
            parent.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            cornerRadius,
            cornerRadius,
            cardPaint
        )
    }
}
