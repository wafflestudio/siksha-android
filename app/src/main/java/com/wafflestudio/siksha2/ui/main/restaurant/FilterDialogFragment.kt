package com.wafflestudio.siksha2.ui.main.restaurant

import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Outline
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.slider.RangeSlider
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.DialogFilterBinding
import kotlinx.coroutines.launch
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class FilterDialogFragment(
    private val mode: FilterMode
) : BottomSheetDialogFragment() {
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    private val vm: DailyRestaurantViewModel by activityViewModels()
    private val defaultCondition = MenuFilterCondition.DEFAULT // Default 값 참고용 (val)
    private var selectedCondition = MenuFilterCondition.DEFAULT // 사용할 값 (var)

    private val categorySet = listOf("전체", "한식", "중식", "분식", "일식", "양식", "아시안", "뷔페")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setOnShowListener {
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            dialog.behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.let {
                val layoutParams = it.layoutParams
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

        setupScrollViewParams()

        setupVisibility()
        setupObservers()

        setupDistanceSelection()
        setupPriceSelection()
        setupRatingSelection()
        setupOperatingSelection()
        setupReviewSelection()
        setupCategorySelection()
        setupButtons()
    }

    private fun setupScrollViewParams() {
        if (mode == FilterMode.FULL) {
            binding.scrollableContent.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        } else {
            binding.scrollableContent.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0f
            )
        }
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.menuFilterCondition.collect { newCondition ->
                selectedCondition = newCondition
                updateCondition()
            }
        }
    }

    private fun updateCondition() {
        if (mode == FilterMode.DISTANCE || mode == FilterMode.FULL) {
            binding.distanceRangeSlider.values = listOf(selectedCondition.distance)
            updateDistanceText(selectedCondition.distance.toInt())
            binding.distanceRangeSlider.post {
                updateDistanceBubblePosition(binding.distanceRangeSlider)
            }
        }
        if (mode == FilterMode.PRICE || mode == FilterMode.FULL) {
            binding.priceRangeSlider.values = listOf(selectedCondition.minPrice, selectedCondition.maxPrice)
            updatePriceRangeText(selectedCondition.minPrice.toInt(), selectedCondition.maxPrice.toInt())
        }
        if (mode == FilterMode.RATING || mode == FilterMode.FULL) {
            updateRatingSelection(selectedCondition.minRating)
        }
        if (mode == FilterMode.CATEGORY || mode == FilterMode.FULL) {
            updateCategorySelection(selectedCondition.categories)
        }
        if (mode == FilterMode.FULL) {
            binding.operatingHoursGroup.check(if (selectedCondition.isOpen) R.id.optionOperating else R.id.optionAll)
            binding.radioGroupReview.check(if (selectedCondition.hasReview) R.id.radioWithReviews else R.id.radioAllReviews)
        }
    }

    private fun setupDistanceSelection() {
        binding.distanceRangeSlider.addOnChangeListener { slider, _, _ ->
            val value = slider.values[0]
            selectedCondition = selectedCondition.copy(distance = value)
            updateDistanceText(selectedCondition.distance.toInt())
            updateDistanceBubblePosition(slider)
        }
    }

    private fun setupPriceSelection() {
        binding.priceRangeSlider.setMinSeparationValue(500.0f)
        binding.priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val (minPrice, maxPrice) = slider.values
            selectedCondition = selectedCondition.copy(minPrice = minPrice, maxPrice = maxPrice)
            updatePriceRangeText(minPrice.toInt(), maxPrice.toInt())
        }
    }

    private fun setupRatingSelection() {
        binding.radioGroupRating.setOnCheckedChangeListener { _, checkedId ->
            val rating = when (checkedId) {
                R.id.radioRatingAll -> 0f
                R.id.radioRating35 -> 3.5f
                R.id.radioRating40 -> 4.0f
                R.id.radioRating45 -> 4.5f
                else -> 0f
            }

            selectedCondition = selectedCondition.copy(minRating = rating)
        }
    }

    private fun setupOperatingSelection() {
        binding.operatingHoursGroup.setOnCheckedChangeListener { _, checkedId ->
            val isOpen = checkedId == R.id.optionOperating
            selectedCondition = selectedCondition.copy(isOpen = isOpen)
        }
    }

    private fun setupReviewSelection() {
        binding.radioGroupReview.setOnCheckedChangeListener { _, checkedId ->
            val hasReview = checkedId == R.id.radioWithReviews
            selectedCondition = selectedCondition.copy(hasReview = hasReview)
        }
    }

    private fun setupCategorySelection() {
        binding.gridCategory.removeAllViews()

        val nanumSquareBold = ResourcesCompat.getFont(requireContext(), R.font.nanum_square_bold)

        categorySet.forEachIndexed { index, category ->
            val chip = Chip(requireContext()).apply {
                text = category
                isCheckable = true
                isClickable = true
                isFocusable = true
                checkedIcon = null

                typeface = nanumSquareBold

                layoutParams = GridLayout.LayoutParams().apply {
                    width = dpToPx(56) // 56dp
                    height = dpToPx(48) // 34dp
                    columnSpec = GridLayout.spec(index % 5)
                    rowSpec = GridLayout.spec(if (index < 5) 0 else 1)
                    setMargins(
                        if (index % 5 != 0) dpToPx(4) else 0,
                        if (index >= 5) dpToPx(4) else 0,
                        if (index % 5 != 4) dpToPx(4) else 0,
                        if (index < 5) dpToPx(4) else 0
                    )
                }
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(10, 10, 10, 10)
                setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text_color))
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(dpToPx(30).toFloat())
                    .build()

                if (category == "전체") {
                    isChecked = true
                    setSelectedCategoryChip(this)
                } else {
                    setUnselectedCategoryChip(this)
                }

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        setSelectedCategoryChip(this)
                    } else {
                        setUnselectedCategoryChip(this)
                    }

                    if (category == "전체") {
                        if (isChecked) {
                            // 전체 선택 시, 다른 카테고리 해제
                            binding.gridCategory.children.forEach { chipView ->
                                (chipView as? Chip)?.takeIf { it.text != "전체" }?.isChecked = false
                            }
                            selectedCondition = selectedCondition.copy(categories = emptySet())
                        } else {
                            // 전체 해제되지만 아무것도 선택 안 되어있으면 다시 전체 체크
                            val anyOtherSelected = binding.gridCategory.children.any { chipView ->
                                (chipView as? Chip)?.takeIf { it.text != "전체" }?.isChecked == true
                            }
                            if (!anyOtherSelected) {
                                this.isChecked = true
                                setSelectedCategoryChip(this)
                                selectedCondition = selectedCondition.copy(categories = emptySet())
                            }
                        }
                    } else {
                        val allChip = binding.gridCategory.children
                            .mapNotNull { it as? Chip }
                            .firstOrNull { it.text == "전체" }

                        if (isChecked) {
                            // 다른 카테고리 선택 시 전체 해제
                            allChip?.isChecked = false
                            selectedCondition = selectedCondition.copy(categories = selectedCondition.categories + category)
                        } else {
                            selectedCondition = selectedCondition.copy(categories = selectedCondition.categories - category)

                            // 아무것도 선택 안 됐으면 전체 선택
                            val anyOtherSelected = binding.gridCategory.children.any { chipView ->
                                (chipView as? Chip)?.takeIf { it.text != "전체" }?.isChecked == true
                            }
                            if (!anyOtherSelected) {
                                allChip?.isChecked = true
                                selectedCondition = selectedCondition.copy(categories = emptySet())
                            }
                        }
                    }
                    selectedCondition = selectedCondition.copy(
                        categories = selectedCondition.categories
                            .filter { it in categorySet }
                            .sortedBy { categorySet.indexOf(it) }
                            .toCollection(LinkedHashSet())
                    )
                }
            }
            binding.gridCategory.addView(chip)
        }
        updateCategorySelection(selectedCondition.categories)
    }

    private fun setupButtons() {
        binding.closeButton.setOnClickListener{
            dismiss()
        }

        binding.btnReset.setOnClickListener {
            resetFiltersByMode()
        }
        binding.btnReset.post { binding.btnReset.setHalfCircleCorners() }

        binding.btnApply.setOnClickListener {
            applyFiltersByMode()
            setFragmentResult("FilterDialog", bundleOf())
            dismiss()
        }
        binding.btnApply.post { binding.btnApply.setHalfCircleCorners() }
    }

    private fun setupVisibility() {
        when (mode) {
            FilterMode.FULL -> {
                binding.dragHandleArea.visibility = View.VISIBLE
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

    private fun updateDistanceText(distance: Int) {
        binding.tvDistanceRange.text = if (distance >= defaultCondition.distance.toInt()) {
            "1km 이상"
        } else {
            "${distance}m 이내"
        }
    }

    private fun updateDistanceBubblePosition(rangeSlider: RangeSlider) {
        val valuePercent = (rangeSlider.values[0] - rangeSlider.valueFrom) / (rangeSlider.valueTo - rangeSlider.valueFrom)
        val valueXDistance = valuePercent * rangeSlider.trackWidth

        val bubbleWidth = binding.distanceBubble.width
        val triangleWidth = binding.distanceTriangle.width

        // 말풍선 중앙값
        val basicBubbleX = rangeSlider.left + rangeSlider.trackSidePadding + valueXDistance - (bubbleWidth / 2f)
        // 말풍선은 화면 양끝으로 제한
        val bubbleX = basicBubbleX.coerceIn(rangeSlider.left.toFloat(), rangeSlider.right.toFloat() - bubbleWidth)
        // 삼각형 기본값 / 삼각형은 ConstranintLayout 시작점을 기준으로 위치를 계산함 (왠진 모르겠)
        val triangleCenterOffset = (bubbleWidth - triangleWidth) / 2f

        binding.distanceBubble.x = bubbleX
        binding.distanceTriangle.x = triangleCenterOffset + (basicBubbleX - bubbleX) // 기본값 + max, min 넘어갔을 때 처리
    }

    private fun updatePriceRangeText(minPrice: Int, maxPrice: Int) {
        val minPriceText = if (minPrice == defaultCondition.minPrice.toInt()) "0원" else "${minPrice}원"
        val maxPriceText = if (maxPrice == defaultCondition.maxPrice.toInt()) "10,000원 이상" else "${maxPrice}원"
        binding.tvPriceRange.text = "$minPriceText ~ $maxPriceText"
    }

    private fun updateRatingSelection(rating: Float) {
        when (rating) {
            0f -> binding.radioGroupRating.check(R.id.radioRatingAll)
            3.5f -> binding.radioGroupRating.check(R.id.radioRating35)
            4.0f -> binding.radioGroupRating.check(R.id.radioRating40)
            4.5f -> binding.radioGroupRating.check(R.id.radioRating45)
        }
    }

    private fun updateCategorySelection(category: Set<String>) {
        binding.gridCategory.children
            .filterIsInstance<Chip>()
            .forEach { chip ->
                val isSelected = if (category.isEmpty()) {
                    chip.text == "전체"
                } else {
                    category.contains(chip.text.toString())
                }

                chip.isChecked = isSelected

                if (isSelected) {
                    setSelectedCategoryChip(chip)
                } else {
                    setUnselectedCategoryChip(chip)
                }
            }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    private fun setSelectedCategoryChip(chip: Chip) {
        chip.apply {
            setChipBackgroundColorResource(R.color.chip_selected_bg)
            chipStrokeWidth = dpToPx(1).toFloat()
            setChipStrokeColorResource(R.color.orange_main)
        }
    }

    private fun setUnselectedCategoryChip(chip: Chip) {
        chip.apply {
            setChipBackgroundColorResource(R.color.chip_default_bg)
            chipStrokeWidth = dpToPx(1).toFloat()
            chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#DFDFDF"))
        }
    }

    private fun View.setHalfCircleCorners() {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val radius = view.height / 2f
//              x  outline.setRoundRect(0, 0, (view.width + radius).toInt(), view.height, radius)
//                outline.setRoundRect((0 - radius).toInt(), 0, view.width, view.height, radius)
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        clipToOutline = true
    }

    private fun resetFiltersByMode() {
        selectedCondition = when (mode) {
            FilterMode.DISTANCE -> selectedCondition.copy(distance = defaultCondition.distance)
            FilterMode.PRICE -> selectedCondition.copy(minPrice = defaultCondition.minPrice, maxPrice = defaultCondition.maxPrice)
            FilterMode.RATING -> selectedCondition.copy(minRating = defaultCondition.minRating)
            FilterMode.CATEGORY -> selectedCondition.copy(categories = defaultCondition.categories)
            FilterMode.FULL -> MenuFilterCondition.DEFAULT
        }

        updateCondition()
    }

    private fun applyFiltersByMode() {
        val originalCondition = vm.getCurrentCondition()

        val applyCondition = when (mode) {
            FilterMode.DISTANCE -> originalCondition.copy(distance = selectedCondition.distance)
            FilterMode.PRICE -> originalCondition.copy(minPrice = selectedCondition.minPrice, maxPrice = selectedCondition.maxPrice)
            FilterMode.RATING -> originalCondition.copy(minRating = selectedCondition.minRating)
            FilterMode.CATEGORY -> originalCondition.copy(categories = selectedCondition.categories)
            FilterMode.FULL -> selectedCondition
        }

        vm.setMenuFilterCondition(applyCondition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
