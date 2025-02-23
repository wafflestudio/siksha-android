package com.wafflestudio.siksha2.ui.main.restaurant

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.wafflestudio.siksha2.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DualRangeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barHeight = 10f // SeekBar 배경 두께
    val thumbRadius = 20f // Thumb 크기 (기존 SeekBar 스타일과 통일)

    private val barPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.gray_500) // SeekBar 배경색
        strokeWidth = barHeight
        isAntiAlias = true
    }

    private val selectedBarPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_main) // 선택된 범위 색상
        strokeWidth = barHeight
        isAntiAlias = true
    }

    private val thumbPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_main) // Thumb 색상
        isAntiAlias = true
    }

    var minValue = 0
    var maxValue = 15000
    var selectedMin = 5000
    var selectedMax = 8000

    private var isMinThumbSelected = false
    private var isMaxThumbSelected = false

    private var listener: ((Int, Int) -> Unit)? = null

    fun setOnRangeChangeListener(listener: (Int, Int) -> Unit) {
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barStart = paddingStart.toFloat() + thumbRadius
        val barEnd = width - paddingEnd.toFloat() - thumbRadius
        val barY = height / 2f

        // SeekBar 배경
        canvas.drawLine(barStart, barY, barEnd, barY, barPaint)

        // Thumb 위치 계산
        val minX = getThumbX(selectedMin)
        val maxX = getThumbX(selectedMax)

        // 선택된 범위 색상
        canvas.drawLine(minX, barY, maxX, barY, selectedBarPaint)

        // 두 개의 Thumb (SeekBar와 통일된 크기)
        canvas.drawCircle(minX, barY, thumbRadius, thumbPaint)
        canvas.drawCircle(maxX, barY, thumbRadius, thumbPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val minX = getThumbX(selectedMin)
                val maxX = getThumbX(selectedMax)

                if (abs(event.x - minX) < thumbRadius) {
                    isMinThumbSelected = true
                } else if (abs(event.x - maxX) < thumbRadius) {
                    isMaxThumbSelected = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val barStart = paddingStart.toFloat() + thumbRadius
                val barEnd = width - paddingEnd.toFloat() - thumbRadius

                if (isMinThumbSelected) {
                    selectedMin = ((x - barStart) / (barEnd - barStart) * maxValue).toInt()
                    selectedMin = max(minValue, min(selectedMin, selectedMax - 500)) // 최소값 제한
                } else if (isMaxThumbSelected) {
                    selectedMax = ((x - barStart) / (barEnd - barStart) * maxValue).toInt()
                    selectedMax = min(maxValue, max(selectedMax, selectedMin + 500)) // 최대값 제한
                }

                listener?.invoke(selectedMin, selectedMax)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isMinThumbSelected = false
                isMaxThumbSelected = false
            }
        }
        return true
    }

    private fun getThumbX(value: Int): Float {
        val barStart = paddingStart.toFloat() + thumbRadius
        val barEnd = width - paddingEnd.toFloat() - thumbRadius
        return barStart + (value.toFloat() / maxValue) * (barEnd - barStart)
    }
}
