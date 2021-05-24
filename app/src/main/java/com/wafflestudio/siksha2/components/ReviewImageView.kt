package com.wafflestudio.siksha2.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.wafflestudio.siksha2.databinding.ItemReviewImageViewBinding

class ReviewImageView : ConstraintLayout {

    private val binding = ItemReviewImageViewBinding.inflate(LayoutInflater.from(context), this)
    private var onDeleteClickListener: OnDeleteClickListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        init()
    }

    private fun init() {
        binding.deleteImageButton.setOnClickListener {
            onDeleteClickListener?.onClick()
        }
    }

    fun setImage(uri: Uri) {
        binding.reviewImage.setImageURI(uri)
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    interface OnDeleteClickListener {
        fun onClick()
    }
}
