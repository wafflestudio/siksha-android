package com.wafflestudio.siksha2.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemMenuFilterBinding

class MenuFilterItem : LinearLayout {
    private val binding = ItemMenuFilterBinding.inflate(LayoutInflater.from(context), this, true)

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

    private fun init(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.MenuFilterItem)
            val text = attributes.getString(R.styleable.MenuFilterItem_filterText) ?: ""
            attributes.recycle()

            setText(text)
        }
    }

    fun setText(text: String) {
        binding.filter.text = text
    }
}
