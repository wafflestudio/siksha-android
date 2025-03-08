package com.wafflestudio.siksha2.ui.main.restaurant

import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.DialogFilterBinding

class FilterDialogFragment(
    private val mode: FilterMode
) : BottomSheetDialogFragment() {
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    private var selectedDistance = 400
    private var selectedMinPrice = 500
    private var selectedMaxPrice = 1500

    private val selectedCategories = mutableSetOf<String>() // 선택된 카테고리 저장
    private val categoryList = listOf("전체", "한식", "중식", "분식", "일식", "양식", "아시안", "뷔페") // 카테고리 목록

    var onFilterApplied: ((FilterData) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setOnShowListener {
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // 높이 크게 설정
                it.layoutParams = layoutParams
            }
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDistanceSection()
        setupPriceSection()
        setupCategorySelection()
        // setupRatings()
        setupButtons()
        setupVisibility()
    }

    private fun setupDistanceSection() {
        binding.seekBarDistance.progress = selectedDistance
        updateDistanceText(selectedDistance)

        binding.seekBarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    val thumbX = calculateThumbX(seekBar, progress)

                    binding.tvDistance.x = thumbX

                    binding.tvDistance.text = "${progress}m 이내"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    private fun calculateThumbX(seekBar: SeekBar, progress: Int): Float {
        val max = seekBar.max
        val availableWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight

        val thumbPosX = seekBar.paddingLeft + (progress.toFloat() / max) * availableWidth

        return thumbPosX - (binding.tvDistance.width / 2)
    }

    private fun updateDistanceText(distance: Int) {
        binding.tvDistance.text = "${distance}m 이내"
    }

    private fun setupPriceSection() {
        binding.dualRangeSeekBar.selectedMin = selectedMinPrice
        binding.dualRangeSeekBar.selectedMax = selectedMaxPrice

        updatePriceRangeText(selectedMinPrice, selectedMaxPrice)

        binding.dualRangeSeekBar.setOnRangeChangeListener { min, max ->
            selectedMinPrice = (min / 500) * 500
            selectedMaxPrice = (max / 500) * 500

            updatePriceRangeText(selectedMinPrice, selectedMaxPrice)
        }
    }

    private fun updatePriceRangeText(minPrice: Int, maxPrice: Int) {
        val maxPriceText = if (maxPrice >= 15000) "15,000원 이상" else "${maxPrice}원"
        binding.tvPriceRange.text = "${minPrice}원 ~ $maxPriceText"

        val minThumbX = calculateThumbX(binding.dualRangeSeekBar, minPrice)
        val maxThumbX = calculateThumbX(binding.dualRangeSeekBar, maxPrice)

        val middleX = (minThumbX + maxThumbX) / 2
        binding.tvPriceRange.x = middleX

        binding.tvPriceRange.translationY = binding.dualRangeSeekBar.y - binding.dualRangeSeekBar.height - 40f
    }

    private fun calculateThumbX(seekBar: View, value: Int): Float {
        val dualSeekBar = seekBar as DualRangeSeekBar
        val max = dualSeekBar.maxValue
        val availableWidth = dualSeekBar.width - dualSeekBar.paddingLeft - dualSeekBar.paddingRight

        val thumbPosX = dualSeekBar.paddingLeft + (value.toFloat() / max) * availableWidth

        return thumbPosX - (binding.tvPriceRange.width / 2)
    }

    private fun setupCategorySelection() {
        val categoryList = listOf("전체", "한식", "중식", "분식", "일식", "양식", "아시안", "뷔페")
        binding.gridCategory.removeAllViews()

        categoryList.forEachIndexed { index, category ->
            val chip = Chip(requireContext()).apply {
                text = category
                isCheckable = true
                isClickable = true
                isFocusable = true
                checkedIcon = null

                layoutParams = GridLayout.LayoutParams().apply {
                    width = dpToPx(56) // 56dp
                    height = dpToPx(42) // 34dp
                    columnSpec = GridLayout.spec(index % 5)
                    rowSpec = GridLayout.spec(if (index < 5) 0 else 1)
                    setMargins(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                }

                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(10, 10, 10, 10)

                setChipBackgroundColorResource(R.color.chip_default_bg)
                setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#DFDFDF")))
                setChipStrokeWidth(1f)
                setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text_color))

                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(dpToPx(30).toFloat())
                    .build()

                if (category == "전체") {
                    isChecked = true
                    setChipBackgroundColorResource(R.color.chip_selected_bg)
                }

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        setChipBackgroundColorResource(R.color.chip_selected_bg)
                        chipStrokeWidth = dpToPx(1).toFloat()
                        setChipStrokeColorResource(R.color.orange_main)
                    } else {
                        setChipBackgroundColorResource(R.color.chip_default_bg)
                        chipStrokeWidth = 0f
                    }

                    if (category == "전체" && isChecked) {
                        binding.gridCategory.children.forEach { chipView ->
                            (chipView as? Chip)?.takeIf { it.text != "전체" }?.isChecked = false
                        }
                        selectedCategories.clear()
                        selectedCategories.add("전체")
                    } else if (isChecked) {
                        val allChip = binding.gridCategory.children
                            .mapNotNull { it as? Chip }
                            .firstOrNull { it.text == "전체" }

                        allChip?.isChecked = false
                        selectedCategories.remove("전체")

                        selectedCategories.add(category)
                    } else {
                        selectedCategories.remove(category)
                    }
                }
            }

            binding.gridCategory.addView(chip)
        }
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    private fun setupButtons() {
        binding.btnReset.setOnClickListener {
            resetFilters()
            for (i in 0 until binding.gridCategory.childCount) {
                val chip = binding.gridCategory.getChildAt(i) as? Chip
                chip?.isChecked = false
            }
        }

        binding.btnApply.setOnClickListener {
            applyFilters()
            dismiss()
        }
    }

    private fun setupVisibility() {
        when (mode) {
            FilterMode.FULL -> {
                binding.distanceSection.visibility = View.VISIBLE
                binding.priceSection.visibility = View.VISIBLE
                binding.openSection.visibility = View.VISIBLE
                binding.reviewSection.visibility = View.VISIBLE
                binding.ratingSection.visibility = View.VISIBLE
                binding.categorySection.visibility = View.VISIBLE
            }
            FilterMode.DISTANCE -> binding.distanceSection.visibility = View.VISIBLE
            FilterMode.PRICE -> binding.priceSection.visibility = View.VISIBLE
            FilterMode.OPEN -> binding.openSection.visibility = View.VISIBLE
            FilterMode.REVIEW -> binding.reviewSection.visibility = View.VISIBLE
            FilterMode.RATING -> binding.ratingSection.visibility = View.VISIBLE
            FilterMode.CATEGORY -> binding.categorySection.visibility = View.VISIBLE
        }
    }

    private fun resetFilters() {
        selectedDistance = 400
        selectedMinPrice = 500
        selectedMaxPrice = 15000
        selectedCategories.clear()
        selectedCategories.add("전체")

        binding.seekBarDistance.progress = selectedDistance

        updateDistanceText(selectedDistance)

        for (i in 0 until binding.gridCategory.childCount) {
            val chip = binding.gridCategory.getChildAt(i) as? Chip
            if (chip != null) {
                chip.isChecked = chip.text == "전체" // '전체' Chip만 유지
            }
        }
    }

    private fun applyFilters() {
        val selectedFilterData = FilterData(
            distance = selectedDistance,
            minPrice = selectedMinPrice,
            maxPrice = selectedMaxPrice,
            categories = if (selectedCategories.contains("전체")) emptyList() else selectedCategories.toList()
        )
        onFilterApplied?.invoke(selectedFilterData)
    }

    data class FilterData(
        val distance: Int,
        val minPrice: Int,
        val maxPrice: Int,
        val categories: List<String>
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
