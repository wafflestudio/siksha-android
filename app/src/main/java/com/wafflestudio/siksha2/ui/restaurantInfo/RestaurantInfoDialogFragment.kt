package com.wafflestudio.siksha2.ui.restaurantInfo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentRestaurantInfoBinding
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.ui.restaurantInfo.model.toRestaurantOperatingTimes
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RestaurantInfoDialogFragment : BottomSheetDialogFragment(), OnMapReadyCallback {

    companion object {
        private const val ARG_RESTAURANT_INFO = "ARG_RESTAURANT_INFO"

        @JvmStatic
        fun newInstance(restaurantInfo: RestaurantInfo) =
            RestaurantInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RESTAURANT_INFO, restaurantInfo)
                }
            }
    }

    private var _binding: FragmentRestaurantInfoBinding? = null
    private val binding get() = _binding!!
    private val restaurantInfo: RestaurantInfo by lazy {
        BundleCompat.getParcelable(requireArguments(), ARG_RESTAURANT_INFO, RestaurantInfo::class.java)!!
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
        _binding = FragmentRestaurantInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.map.onCreate(null)
        binding.map.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheetBehavior()
        initView()
        initData()
        initClickListener()
    }

    private fun initBottomSheetBehavior() {
        (dialog as BottomSheetDialog).behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initView() {
        binding.title.text = restaurantInfo.nameKr
        binding.subtitle.text = restaurantInfo.address?.replace("서울 관악구 관악로 1", "") ?: ""
    }

    private fun initData() {
        binding.restaurantOperatingTimes = restaurantInfo.etc?.operatingHours?.toRestaurantOperatingTimes() // TODO: RestaurantInfo단부터 DTO 대신 UiState 만들어 사용하기
    }

    private fun initClickListener() {
        binding.closeButton.setOnClickListener { dismiss() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney and move the camera
        MapsInitializer.initialize(requireContext())
        val position = LatLng(restaurantInfo.latitude ?: 0.0, restaurantInfo.longitude ?: 0.0)
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.5f))
            addMarker(MarkerOptions().position(position))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }
}
