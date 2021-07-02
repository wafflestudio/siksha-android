package com.wafflestudio.siksha2.ui.restaurant_info

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.subtitle.text = restaurantInfo.address?.replace("서울 관악구 관악로 1", "") ?: ""
        restaurantInfo.etc?.operatingHours?.let {
            setUpOperatingHour(it)
        }
        binding.closeButton.setOnClickListener { dismiss() }
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
        val items = listOf(binding.timeLayoutWeekday, binding.timeLayoutSaturday, binding.timeLayoutHoliday)
        if (operatingHour.weekdays.isNotEmpty()) {
            items[0].apply {
                visibility = View.VISIBLE
                binding.timeTextWeekday.text = operatingHour.weekdays.joinToString("\n")
            }
        }

        if (operatingHour.saturday.isNotEmpty()) {
            items[1].apply {
                visibility = View.VISIBLE
                binding.timeTextSaturday.text = operatingHour.saturday.joinToString("\n")
                binding.lineDivFirst.visibility = View.VISIBLE
            }
        }

        if (operatingHour.holiday.isNotEmpty()) {
            items[2].apply {
                visibility = View.VISIBLE
                binding.timeTextHoliday.text = operatingHour.holiday.joinToString("\n")
                binding.lineDivSecond.visibility = View.VISIBLE
            }
        }
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
