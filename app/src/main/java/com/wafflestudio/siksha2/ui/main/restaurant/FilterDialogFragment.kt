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
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
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

    private val vm: DailyRestaurantViewModel by activityViewModels()

    private var selectedDistance: Float = 1000f
    private var selectedMinPrice: Float = 3000f
    private var selectedMaxPrice: Float = 10000f
    private var selectedOperating: Boolean = false
    private var selectedReview: Boolean = false
    private var selectedRating: Float = 0f
    private val selectedCategories = mutableListOf<String>()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupSeekBarListeners()
        setupRatingSelection()
        setupOperatingSelection()
        setupReviewSelection()
        setupCategorySelection()
        setupButtons()
        setupVisibility()
    }

    private fun setupObservers() {
        vm.menuFilterCondition.observe(viewLifecycleOwner) { filterCondition ->
            if (mode == FilterMode.DISTANCE || mode == FilterMode.FULL) {
                selectedDistance = filterCondition.distance ?: 1000f
                binding.distanceRangeSlider.values = listOf(selectedDistance)
                updateDistanceText(selectedDistance.toInt())
            }
            if (mode == FilterMode.PRICE || mode == FilterMode.FULL) {
                selectedMinPrice = filterCondition.minPrice ?: 3000f
                selectedMaxPrice = filterCondition.maxPrice ?: 10000f
                binding.priceRangeSlider.valueFrom = selectedMinPrice
                binding.priceRangeSlider.valueTo = selectedMaxPrice
                updatePriceRangeText(selectedMinPrice.toInt(), selectedMaxPrice.toInt())
            }
            if (mode == FilterMode.RATING || mode == FilterMode.FULL) {
                selectedRating = filterCondition.minRating ?: 0f
                updateRatingSelection(selectedRating)
            }
            if (mode == FilterMode.CATEGORY || mode == FilterMode.FULL) {
                selectedCategories.clear()
                selectedCategories.addAll(filterCondition.categories ?: emptyList())
            }
            if (mode == FilterMode.FULL) {
                selectedOperating = filterCondition.isOpen
                selectedReview = filterCondition.hasReview
                binding.operatingHoursGroup.check(if (selectedOperating) R.id.optionOperating else R.id.optionAll)
                binding.radioGroupReview.check(if (selectedReview) R.id.radioWithReviews else R.id.radioAllReviews)
            }
        }
    }

    private fun setupSeekBarListeners() {
        if (mode == FilterMode.DISTANCE || mode == FilterMode.FULL) {
//            binding.seekBarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    if (fromUser) {
//                        selectedDistance = progress.toFloat()
//                        updateDistanceText(progress)
//                    }
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//            })
            binding.distanceRangeSlider.addOnChangeListener { slider, _, _ ->
                val value = slider.values[0]
                updateDistanceText(value.toInt())
            }
        }

        if (mode == FilterMode.PRICE || mode == FilterMode.FULL) {
            binding.priceRangeSlider.addOnChangeListener { slider, _, _ ->
                val values = slider.values
                updatePriceRangeText(values[0].toInt(), values[1].toInt())
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    private fun updateDistanceText(distance: Int) {
        binding.tvDistance.text = if (distance >= 1000) "1km 이상" else "${distance}m 이내"
    }

    private fun updatePriceRangeText(minPrice: Int, maxPrice: Int) {
        val minPriceText = if (minPrice <= 3000) "3,000원 이하" else "${minPrice}원"
        val maxPriceText = if (maxPrice >= 10000) "10,000원 이상" else "${maxPrice}원"
        binding.tvPriceRange.text = "$minPriceText ~ $maxPriceText"
    }

    private fun setupRatingSelection() {
        binding.radioGroupRating.setOnCheckedChangeListener { _, checkedId ->
            selectedRating = when (checkedId) {
                R.id.radioRatingAll -> 0f
                R.id.radioRating35 -> 3.5f
                R.id.radioRating40 -> 4.0f
                R.id.radioRating45 -> 4.5f
                else -> 0f
            }
        }
    }

    private fun updateRatingSelection(rating: Float) {
        when (rating) {
            0f -> binding.radioGroupRating.check(R.id.radioRatingAll)
            3.5f -> binding.radioGroupRating.check(R.id.radioRating35)
            4.0f -> binding.radioGroupRating.check(R.id.radioRating40)
            4.5f -> binding.radioGroupRating.check(R.id.radioRating45)
        }
    }

    private fun setupOperatingSelection() {
        binding.operatingHoursGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedOperating = checkedId == R.id.optionOperating
        }
    }

    private fun setupReviewSelection() {
        binding.radioGroupReview.setOnCheckedChangeListener { _, checkedId ->
            selectedReview = checkedId == R.id.radioWithReviews
        }
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
                chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#DFDFDF"))
                chipStrokeWidth = 1f
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

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    private fun setupButtons() {
        binding.btnReset.setOnClickListener {
            resetFiltersByMode()
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            applyFiltersByMode()
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
            FilterMode.RATING -> binding.ratingSection.visibility = View.VISIBLE
            FilterMode.CATEGORY -> binding.categorySection.visibility = View.VISIBLE
        }
    }

    private fun resetFiltersByMode() {
        when (mode) {
            FilterMode.DISTANCE -> {
                selectedDistance = 1000f
                vm.setMenuFilterCondition(vm.getCurrentCondition().copy(distance = null))
            }
            FilterMode.PRICE -> {
                selectedMinPrice = 3000f
                selectedMaxPrice = 10000f
                vm.setMenuFilterCondition(vm.getCurrentCondition().copy(minPrice = null, maxPrice = null))
            }
            FilterMode.RATING -> {
                selectedRating = 0f
                updateRatingSelection(0f)
                vm.setMenuFilterCondition(vm.getCurrentCondition().copy(minRating = null))
            }
            FilterMode.CATEGORY -> {
                selectedCategories.clear()
                vm.setMenuFilterCondition(vm.getCurrentCondition().copy(categories = null))
            }
            FilterMode.FULL -> {
                selectedDistance = 1000f
                selectedMinPrice = 3000f
                selectedMaxPrice = 10000f
                selectedOperating = false
                selectedReview = false
                selectedRating = 0f
                selectedCategories.clear()
                vm.setMenuFilterCondition(
                    MenuFilterCondition(
                        null,
                        null,
                        null,
                        false,
                        false,
                        null,
                        null
                    )
                )
            }
        }
    }

    private fun applyFiltersByMode() {
        val updatedCondition = when (mode) {
            FilterMode.DISTANCE -> vm.getCurrentCondition().copy(
                distance = selectedDistance
            )
            FilterMode.PRICE -> vm.getCurrentCondition().copy(
                minPrice = selectedMinPrice,
                maxPrice = selectedMaxPrice
            )
            FilterMode.RATING -> vm.getCurrentCondition().copy(
                minRating = if (selectedRating == 0f) null else selectedRating
            )
            FilterMode.CATEGORY -> vm.getCurrentCondition().copy(
                categories = if (selectedCategories.contains("전체")) null else selectedCategories
            )
            FilterMode.FULL -> vm.getCurrentCondition().copy(
                distance = selectedDistance,
                minPrice = selectedMinPrice,
                maxPrice = selectedMaxPrice,
                isOpen = selectedOperating,
                hasReview = selectedReview,
                minRating = if (selectedRating == 0f) null else selectedRating,
                categories = if (selectedCategories.contains("전체")) null else selectedCategories
            )
        }
        vm.setMenuFilterCondition(updatedCondition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
