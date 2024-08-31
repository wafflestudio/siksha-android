package com.wafflestudio.siksha2.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat.getString
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.utils.showToast
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

    private var initialScale: Float? = null
    private val savedMatrix = Matrix()
    private val savedPoint = PointF()
    private val savedMidPoint = PointF()
    private var savedDist = 1f
    private var mode = Mode.NONE

    init {
        scaleType = ScaleType.MATRIX
    }

    /**
     * 노출할 이미지의 url을 지정합니다.
     *
     * @param image 이미지의 웹 url
     */
    fun setModel(image: String) {
        val placeHolderDrawable = CircularProgressDrawable(context).apply {
            setColorSchemeColors(context.getColor(R.color.orange_main))
            strokeWidth = 10f
            centerRadius = 40f
            start()
        }

        Glide.with(this)
            .load(image)
            .placeholder(placeHolderDrawable)
            .fallback(R.drawable.frame_black)
            .error(R.drawable.frame_black)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    context.showToast(getString(context, R.string.common_network_error))
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    post {
                        setCenterInside()
                        initialScale = imageMatrix.scaleX
                    }
                    return false
                }
            })
            .into(this)
    }

    /**
     * [ScaleType.CENTER_INSIDE]를 적용합니다.
     *
     * [setScaleType(ScaleType.CENTER_INSIDE)]와 동일한 효과를 [ScaleType.MATRIX]로 얻습니다.
     */
    private fun setCenterInside() {
        val imageViewWidth = width.toFloat()
        val imageViewHeight = height.toFloat()
        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight

        val scale: Float = if (imageWidth / imageViewWidth > imageHeight / imageViewHeight) {
            imageViewWidth / imageWidth
        } else {
            imageViewHeight / imageHeight
        }

        // 스케일 변환 후 중앙 위치 계산
        val dx = (imageViewWidth - imageWidth * scale) / 2
        val dy = (imageViewHeight - imageHeight * scale) / 2

        // Matrix 설정
        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(dx, dy)
        }

        // ImageView에 Matrix 적용
        imageMatrix = matrix
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        if (drawable == null) return true

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
     * 원본 이미지보다 작게 축소하지 않는 한, 점 ([pivotX], [pivotY])를 기준으로 [requestedScale]만큼 scale한 행렬을 반환합니다.
     */
    private fun Matrix.scaled(requestedScale: Float, pivotX: Float, pivotY: Float): Matrix {
        val initialScale = initialScale ?: return this
        val dScale = requestedScale.coerceAtLeast(initialScale / this.scaleX)

        return Matrix().apply {
            set(this@scaled)
            postTranslate(-pivotX, -pivotY)
            postScale(dScale, dScale)
            postTranslate(pivotX, pivotY)
        }
    }

    /**
     * 여백이 보이지 않는 한 [requestedDx], [requestedDy]만큼 translate한 행렬을 반환합니다.
     */
    private fun Matrix.translated(requestedDx: Float, requestedDy: Float): Matrix {
        val currentX = this.translationX
        val scaledImageWidth = drawable.intrinsicWidth * this.scaleX
        val viewWidth = width
        val dx = if (viewWidth > scaledImageWidth) {
            (viewWidth - scaledImageWidth) / 2 - currentX
        } else {
            requestedDx.coerceIn(viewWidth - scaledImageWidth - currentX, -currentX)
        }

        val currentY = this.translationY
        val scaledImageHeight = drawable.intrinsicHeight * this.scaleX
        val viewHeight = height
        val dy =
            if (viewHeight > scaledImageHeight) {
                (viewHeight - scaledImageHeight) / 2 - currentY
            } else {
                requestedDy.coerceIn(viewHeight - scaledImageHeight - currentY, -currentY)
            }

        return Matrix().apply {
            set(this@translated)
            postTranslate(dx, dy)
        }
    }

    /**
     * [event]의 두 점 사이 거리를 반환합니다.
     */
    private fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    /**
     * [event]의 두 점 사이 중점을 계산하여 point에 설정합니다.
     */
    private fun setMidPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        val currentX = imageMatrix.translationX
        val scaledImageWidth = drawable.intrinsicWidth * imageMatrix.scaleX
        val viewWidth = width
        val res = if (direction > 0) {
            currentX + scaledImageWidth > viewWidth
        } else {
            currentX < 0
        }
        return res
    }
}
