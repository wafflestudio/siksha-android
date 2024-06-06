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

    private val savedMatrix = Matrix()
    private val savedPoint = PointF()
    private val savedMidPoint = PointF()
    private var savedDist = 1f
    private var mode = Mode.NONE

    private val gestureDetector = GestureDetector(
        context,
        object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                return true
            }
        }
    )

    init {
        scaleType = ScaleType.MATRIX
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        if (drawable == null) return true

        gestureDetector.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(imageMatrix)
                savedPoint.set(event.x, event.y)
                mode = Mode.DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                savedDist = getDistance(event)
                if (savedDist > 10f) {
                    savedMatrix.set(imageMatrix)
                    setMidPoint(savedMidPoint, event)
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
                        imageMatrix = savedMatrix.translated(event.x - savedPoint.x, event.y - savedPoint.y)
                    }

                    Mode.ZOOM -> {
                        val newDist = getDistance(event)
                        if (newDist > 10f) {
                            imageMatrix = savedMatrix
                                .scaled(newDist / savedDist, savedMidPoint.x, savedMidPoint.y)
                                .translated(
                                    (event.getX(0) + event.getX(1)) / 2 - savedMidPoint.x,
                                    (event.getY(0) + event.getY(1)) / 2 - savedMidPoint.y
                                )
                        }
                    }

                    Mode.NONE -> Unit
                }
            }
        }
        return true
    }

    /**
     * 원본 이미지보다 작게 축소하지 않는 한 (pivotX, pivotY)를 기준으로 requestedScale만큼 scale한 행렬을 반환합니다.
     */
    private fun Matrix.scaled(requestedScale: Float, pivotX: Float, pivotY: Float): Matrix {
        val scale = requestedScale.coerceAtLeast(1f / this.scaleX)
        return Matrix().apply {
            set(this@scaled)
            postTranslate(-pivotX, -pivotY)
            postScale(scale, scale)
            postTranslate(pivotX, pivotY)
        }
    }

    /**
     * 여백이 보이지 않는 한 requestedDx, requestedDy만큼 translate한 행렬을 반환합니다.
     */
    private fun Matrix.translated(requestedDx: Float, requestedDy: Float): Matrix {
        val imageWidth = drawable.intrinsicWidth
        val scaledImageWidth = imageWidth * this.scaleX
        val dxLimit = scaledImageWidth - imageWidth
        if (dxLimit < 0) return this // FIXME: scaled()에서 coerceAtLeast 했음에도 불구하고 scaleX >= 1인 경우가 있다. scaled() 단에서 막도록 수정 필요
        val dx = requestedDx.coerceIn(-dxLimit - this.translationX, -this.translationX)

        val imageHeight = drawable.intrinsicHeight
        val scaledImageHeight = imageHeight * this.scaleX
        val dyLimit = scaledImageHeight - imageHeight
        val dy = requestedDy.coerceIn(-dyLimit - this.translationY, -this.translationY)

        return Matrix().apply {
            set(this@translated)
            postTranslate(dx, dy)
        }
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

private val Matrix.scaleX get() = run {
    val values = FloatArray(9)
    getValues(values)
    values[Matrix.MSCALE_X]
}

private val Matrix.translationX get() = run {
    val values = FloatArray(9)
    getValues(values)
    values[Matrix.MTRANS_X]
}

private val Matrix.translationY get() = run {
    val values = FloatArray(9)
    getValues(values)
    values[Matrix.MTRANS_Y]
}
