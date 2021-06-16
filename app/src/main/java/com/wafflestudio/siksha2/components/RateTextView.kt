package com.wafflestudio.siksha2.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemRateTextViewBinding
import com.wafflestudio.siksha2.utils.StringFormatter

class RateTextView : LinearLayout {

    private val binding = ItemRateTextViewBinding.inflate(LayoutInflater.from(context), this)
    var rate: Double = 0.0
        set(value) {
            if (field == value) return
            if (value == 0.0) {
                setTextNull()
            } else {
                setText(value)
            }

            field = value
        }

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
        setTextNull()
    }

    private fun setTextNull() {
        binding.menuScore.text = "-"
        binding.menuScore.setTextColor(resources.getColor(R.color.black, null))
        binding.menuScore.setBackgroundColor(resources.getColor(R.color.white, null))
    }

    private fun setText(value: Double) {
        binding.menuScore.text = StringFormatter.formatScore(value)
        binding.menuScore.setTextColor(resources.getColor(R.color.white, null))

        if (value <= 3.0) binding.menuScore.setBackgroundResource(R.drawable.frame_rate_text_yellow)
        else if (value > 3.0 && value <= 4.0) binding.menuScore.setBackgroundResource(R.drawable.frame_rate_text_orange)
        else binding.menuScore.setBackgroundResource(R.drawable.frame_rate_text_red)
    }
}
