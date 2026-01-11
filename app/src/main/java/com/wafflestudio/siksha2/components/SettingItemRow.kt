package com.wafflestudio.siksha2.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemSettingRowBinding
import com.wafflestudio.siksha2.utils.setVisibleOrGone

class SettingItemRow : LinearLayout {
    private val binding = ItemSettingRowBinding.inflate(LayoutInflater.from(context), this)
    var checked: Boolean = false
        get() = binding.checkbox.isSelected
        set(value) {
            binding.checkbox.isSelected = value
            field = value
        }

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

    fun setNewIcon(visible: Boolean) {
        binding.newIcon.setVisibleOrGone(visible)
        requestLayout()
        invalidate()
    }

    fun setArrowIcon(visible: Boolean) {
        binding.arrowIcon.setVisibleOrGone(visible)
        requestLayout()
        invalidate()
    }

    fun setShowCheckbox(visible: Boolean) {
        binding.checkbox.setVisibleOrGone(visible)
        requestLayout()
        invalidate()
    }

    fun setShowCheckSimple(visible: Boolean) {
        binding.checkSimple.setVisibleOrGone(visible)
        requestLayout()
        invalidate()
    }

    fun setShowToggleSwitch(visible: Boolean) {
        binding.toggleSwitch.setVisibleOrGone(visible)

        isClickable = false
        isFocusable = false
    }

    fun setToggleState(active: Boolean) {
        binding.toggleSwitch.setActive(active)
    }

    fun setOnToggleClicked(listener: (Boolean) -> Unit) {
        binding.toggleSwitch.setOnClickListener {
            binding.toggleSwitch.toggle()
            listener(binding.toggleSwitch.isActive)
        }
    }

    private fun init(attr: AttributeSet?) {
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL

        context.theme.obtainStyledAttributes(
            attr,
            R.styleable.SettingItem,
            0,
            0
        ).apply {
            try {
                setArrowIcon(getBoolean(R.styleable.SettingItem_showArrowIcon, true))
                setNewIcon(getBoolean(R.styleable.SettingItem_showNewIcon, false))
                setShowCheckbox(getBoolean(R.styleable.SettingItem_showCheckbox, false))
                setShowCheckSimple(getBoolean(R.styleable.SettingItem_showCheckSimple, false))
                setShowToggleSwitch(getBoolean(R.styleable.SettingItem_showToggleSwitch, false))

                binding.settingRowText.text = getString(R.styleable.SettingItem_itemText)
                binding.settingRowText.setTextColor(context.obtainStyledAttributes(attr, R.styleable.SettingItem).getColor(R.styleable.SettingItem_textColor, ContextCompat.getColor(context, R.color.black)))
            } finally {
                recycle()
            }
        }
    }
}
