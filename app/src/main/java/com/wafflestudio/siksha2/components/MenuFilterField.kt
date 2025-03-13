package com.wafflestudio.siksha2.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemMenuFilterFieldBinding

class MenuFilterField : LinearLayout {
    private val binding = ItemMenuFilterFieldBinding.inflate(LayoutInflater.from(context), this, true)

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
            val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.MenuFilterField)
            val text = attributes.getString(R.styleable.MenuFilterField_filterText) ?: ""
            val showArrow = attributes.getBoolean(R.styleable.MenuFilterField_showArrow, false)
            val showCheck = attributes.getBoolean(R.styleable.MenuFilterField_showCheck, false)
            attributes.recycle()

            showArrow(showArrow)
            showCheck(showCheck)
            setFilter(text, true)
        }
    }

    fun showArrow(show: Boolean) {
        binding.arrowDown.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun showCheck(show: Boolean) {
        binding.check.visibility = if (show) View.VISIBLE else View.GONE
        updateStyle(!show) // check 표시가 있으면 active
    }

    fun setFilter(text: String, isNull: Boolean) {
        binding.filter.text = text
        updateStyle(isNull)
    }

    private fun updateStyle(isNull: Boolean) {
        val backgroundRes = if (isNull) {
            R.drawable.frame_menu_filter_field
        } else {
            R.drawable.frame_menu_filter_field_active
        }
        binding.filterBackground.background = ContextCompat.getDrawable(context, backgroundRes)
    }
}
