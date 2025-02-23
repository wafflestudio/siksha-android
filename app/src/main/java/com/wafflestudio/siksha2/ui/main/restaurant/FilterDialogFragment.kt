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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.bottomnavigation.LabelVisibilityMode
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
        binding.seekBarMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // 500원 단위로 반올림
                    val roundedProgress = (progress / 500) * 500
                    binding.seekBarMin.progress = roundedProgress

                    // 최소값이 최대값보다 커지지 않도록 보정
                    if (binding.seekBarMin.progress >= binding.seekBarMax.progress - 500) {
                        binding.seekBarMin.progress = binding.seekBarMax.progress - 500
                    }
                    updatePriceRangeText() // 최소값 변경 후 말풍선 업데이트
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        binding.seekBarMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    var roundedProgress = (progress / 500) * 500

                    // ✅ 최대값이 최소값보다 작아지지 않도록 보정
                    if (roundedProgress <= binding.seekBarMin.progress + 500) {
                        roundedProgress = binding.seekBarMin.progress + 500
                    }

                    // ✅ 값이 변경될 때만 업데이트
                    if (binding.seekBarMax.progress != roundedProgress) {
                        binding.seekBarMax.progress = roundedProgress
                        updatePriceRangeText()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updatePriceRangeText() {
        val minPrice = binding.seekBarMin.progress
        val maxPrice = binding.seekBarMax.progress

        // 15,000원이 넘으면 "15,000원 이상"으로 표시
        val maxPriceText = if (maxPrice >= 15000) {
            "15,000원 이상"
        } else {
            "${maxPrice}원"
        }

        binding.tvPriceRange.text = "${minPrice}원 ~ $maxPriceText"

        // 중앙 말풍선 위치 계산
        val minThumbX = calculateThumbXPrice(binding.seekBarMin, minPrice)
        val maxThumbX = calculateThumbXPrice(binding.seekBarMax, maxPrice)

        val middleX = (minThumbX + maxThumbX) / 2 // 두 thumb의 중간 좌표 계산

        // 중앙 말풍선 위치 업데이트
        binding.tvPriceRange.translationX = middleX
        binding.tvPriceRange.translationY = binding.seekBarMax.y - 100f // thumb 위로 배치
    }

    private fun calculateThumbXPrice(seekBar: SeekBar, progress: Int): Float {
        val max = seekBar.max
        val availableWidth = seekBar.width - seekBar.paddingLeft - seekBar.paddingRight

        // 실제 thumb 위치 계산
        val thumbPosX = seekBar.paddingLeft + (progress.toFloat() / max) * availableWidth

        // tvPriceRange의 width를 고려하여 가운데 정렬
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
                    width = dpToPx(56)  // 56dp
                    height = dpToPx(34) // 34dp
                    columnSpec = GridLayout.spec(index % 5)
                    rowSpec = GridLayout.spec(if (index < 5) 0 else 1)
                    setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
                }

                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(10, 10, 10, 10)

                setChipBackgroundColorResource(R.color.chip_default_bg)
                setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#DFDFDF")))
                setChipStrokeWidth(1f)
                setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text_color))

                chipCornerRadius = dpToPx(30).toFloat()

                if (category == "전체") {
                    isChecked = true
                    setChipBackgroundColorResource(R.color.chip_selected_bg)
                }

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        setChipBackgroundColorResource(R.color.chip_selected_bg)
                    } else {
                        setChipBackgroundColorResource(R.color.chip_default_bg)
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
        updatePriceRangeText()

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
