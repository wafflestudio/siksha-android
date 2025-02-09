package com.wafflestudio.siksha2.ui.main.restaurant

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.siksha2.databinding.DialogFilterBinding

class FilterDialogFragment(
    private val mode: String,
    private val onFilterApplied: (Int, Int, Int) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    private var selectedDistance = 400
    private var selectedMinPrice = 5000
    private var selectedMaxPrice = 8000

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 거리 값 설정
        binding.seekBarDistance.progress = selectedDistance
        binding.tvDistance.text = "${selectedDistance}m 이내"

        // 거리 조절 리스너
        binding.seekBarDistance.setOnSeekBarChangeListener(object :
                android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                    selectedDistance = progress
                    binding.tvDistance.text = "${progress}m 이내"
                }
                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
            })

        // 초기화 버튼
        binding.btnReset.setOnClickListener {
            resetFilters()
        }

        // 적용 버튼
        binding.btnApply.setOnClickListener {
            onFilterApplied(selectedDistance, selectedMinPrice, selectedMaxPrice)
            dismiss()
        }

        if (mode == "distance_only") {
            binding.priceSection.visibility = View.GONE
            binding.openSection.visibility = View.GONE
            binding.reviewSection.visibility = View.GONE
            binding.ratingSection.visibility = View.GONE
        }
    }

    private fun resetFilters() {
        selectedDistance = 400
        selectedMinPrice = 5000
        selectedMaxPrice = 8000

        binding.seekBarDistance.progress = selectedDistance
        binding.tvDistance.text = "${selectedDistance}m 이내"
    }
}
