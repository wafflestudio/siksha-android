package com.wafflestudio.siksha2.ui.restaurant_info

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentRestaurantInfoBinding
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.utils.dp
import com.wafflestudio.siksha2.utils.visibleOrGone
import dagger.hilt.android.AndroidEntryPoint

// TODO: 전반적으로 대충 짬 나중에 날잡고 고치기
@AndroidEntryPoint
class RestaurantInfoDialogFragment private constructor() :
    BottomSheetDialogFragment(),
    OnMapReadyCallback {

    private lateinit var binding: FragmentRestaurantInfoBinding
    private val restaurantInfo: RestaurantInfo by lazy {
        arguments?.getParcelable<RestaurantInfo>(
            RESTAURANT_INFO
        )!!
    }

    private val selectedOperatingHour = MutableLiveData<DayType>(DayType.WEEKDAY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.map.onCreate(null)
        binding.map.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = restaurantInfo.nameKr
        binding.subtitle.text = restaurantInfo.address
        selectedOperatingHour.observe(viewLifecycleOwner) {
            binding.operationTime.text = when (it!!) {
                DayType.WEEKDAY -> restaurantInfo.etc?.operatingHours?.weekdays
                DayType.SATURDAY -> restaurantInfo.etc?.operatingHours?.saturday
                DayType.HOLIDAY -> restaurantInfo.etc?.operatingHours?.holiday
            }?.joinToString("\n")
        }
        restaurantInfo.etc?.operatingHours?.let {
            setUpOperatingHour(it)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // Add a marker in Sydney and move the camera
        MapsInitializer.initialize(context)
        val position = LatLng(restaurantInfo.latitude ?: 0.0, restaurantInfo.longitude ?: 0.0)
        with(googleMap!!) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.5f))
            addMarker(MarkerOptions().position(position))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    private fun setUpOperatingHour(operatingHour: RestaurantInfo.OperatingHour) {
        var ind = 0
        val items = listOf(binding.timeTableStart, binding.timeTableCenter, binding.timeTableEnd)
        if (operatingHour.weekdays.isNotEmpty()) {
            items[ind].apply {
                text = "주중"
                setOnClickListener {
                    selectedOperatingHour.value = DayType.WEEKDAY
                    items.forEach { item -> item.isSelected = false }
                    isSelected = true
                }
            }
            ind++
        }

        if (operatingHour.saturday.isNotEmpty()) {
            items[ind].apply {
                text = "토요일"
                setOnClickListener {
                    selectedOperatingHour.value = DayType.SATURDAY
                    items.forEach { item -> item.isSelected = false }
                    isSelected = true
                }
            }
            ind++
        }

        if (operatingHour.holiday.isNotEmpty()) {
            items[ind].apply {
                text = "휴일"
                setOnClickListener {
                    selectedOperatingHour.value = DayType.HOLIDAY
                    items.forEach { item -> item.isSelected = false }
                    isSelected = true
                }
            }
            ind++
        }
        items.subList(ind, items.size).forEach { it.visibleOrGone(false) }
        items.subList(0, ind).apply {
            forEachIndexed { index, it ->
                it.visibleOrGone(true)
                it.setBackgroundResource(R.drawable.frame_corner_center)
                it.translationX = -requireContext().dp(index).toFloat()
            }
            first().setBackgroundResource(R.drawable.frame_corner_left)
            last().setBackgroundResource(R.drawable.frame_corner_right)
            first().isSelected = true
        }
        if (ind == 1) items.first().setBackgroundResource(R.drawable.frame_corner_all)
    }

    private enum class DayType {
        WEEKDAY,
        SATURDAY,
        HOLIDAY
    }

    companion object {
        const val RESTAURANT_INFO = "restaurant_info"

        @JvmStatic
        fun newInstance(restaurantInfo: RestaurantInfo) =
            RestaurantInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESTAURANT_INFO, restaurantInfo)
                }
            }
    }
}
