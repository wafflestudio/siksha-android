package com.wafflestudio.siksha2.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.sqrt

class ScalableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    enum class Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private val start = PointF()
    private val startMid = PointF()
    private var oldDist = 1f
    private var mode = Mode.NONE

    private val currentScale get() = run {
        val values = FloatArray(9)
        imageMatrix.getValues(values)
        values[Matrix.MSCALE_X]
    }

    private val gestureDetector = GestureDetector(
        context,
        object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                matrix.setScale(1f, 1f)
                matrix.setTranslate(0f, 0f)
                imageMatrix = matrix
                return true
            }
        }
    )

    init {
        imageMatrix = matrix
        scaleType = ScaleType.MATRIX
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        gestureDetector.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                mode = Mode.DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = getDistance(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    setMidPoint(startMid, event)
                    mode = Mode.ZOOM
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                mode = Mode.NONE
            }

            MotionEvent.ACTION_MOVE -> {
                when (mode) {
                    Mode.DRAG -> {
                        matrix.set(savedMatrix)
                        matrix.postTranslate(event.x - start.x, event.y - start.y)
                    }

                    Mode.ZOOM -> {
                        val newDist = getDistance(event)
                        if (newDist > 10f) {
                            matrix.set(savedMatrix)
                            val scale = (newDist / oldDist).coerceAtLeast(1f / currentScale)
                            matrix.postScale(scale, scale, startMid.x, startMid.y)

                            val dx = (event.getX(0) + event.getX(1)) / 2 - startMid.x
                            val dy = (event.getY(0) + event.getY(1)) / 2 - startMid.y
                            matrix.postTranslate(dx, dy)
                        }
                    }

                    Mode.NONE -> Unit
                }
            }
        }
        imageMatrix = matrix
        return true
    }

    /**
     * event의 두 점 사이 거리를 반환합니다.
     */
    private fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    /**
     * event의 두 점 사이 중점을 계산하여 point에 설정합니다.
     */
    private fun setMidPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }
}
