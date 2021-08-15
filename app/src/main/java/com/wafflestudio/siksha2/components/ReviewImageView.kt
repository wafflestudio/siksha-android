package com.wafflestudio.siksha2.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemReviewImageViewBinding
import com.wafflestudio.siksha2.ui.menu_detail.ReviewImageDialog
import com.wafflestudio.siksha2.utils.setImageUrl
import com.wafflestudio.siksha2.utils.visibleOrGone

class ReviewImageView : ConstraintLayout {

    private val binding = ItemReviewImageViewBinding.inflate(LayoutInflater.from(context), this)
    private var onDeleteClickListener: OnDeleteClickListener? = null

    private var imageUrl: String? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        init(attributeSet)
    }

    private fun init(attr: AttributeSet?) {

        context.theme.obtainStyledAttributes(
            attr,
            R.styleable.ReviewImageView,
            0,
            0
        ).apply {
            try {
                binding.deleteImageButton.visibleOrGone(getBoolean(R.styleable.ReviewImageView_showDeleteIcon, true))
            } finally {
                recycle()
            }
        }

        binding.deleteImageButton.setOnClickListener {
            onDeleteClickListener?.onClick()
        }
    }

    fun setImage(uri: Uri) {
        binding.reviewImage.setImageURI(uri)
        requestLayout()
        invalidate()
    }

    fun setImage(url: String) {
        binding.reviewImage.setImageUrl(url)
        imageUrl = url
    }

    fun setImageClickListener(listener: (String) -> (Unit)) {
        binding.reviewImage.setOnClickListener {
            imageUrl?.let { url ->
                listener.invoke(url)
            }
        }
    }

    fun showMorePhotos(photoCount: Long) {
        binding.morePhotoLayout.visibility = View.VISIBLE
        binding.textMorePhoto.text = photoCount.toString()
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    interface OnDeleteClickListener {
        fun onClick()
    }
}
