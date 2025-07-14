package com.wafflestudio.siksha2.ui.restaurantInfo

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.os.BundleCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.BottomsheetRestaurantInfoBinding
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.ui.restaurantInfo.model.toRestaurantOperatingTimes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantInfoBottomSheet : BottomSheetDialogFragment(), OnMapReadyCallback {

    companion object {
        private const val ARG_RESTAURANT_INFO = "ARG_RESTAURANT_INFO"

        fun newInstance(restaurantInfo: RestaurantInfo) =
            RestaurantInfoBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RESTAURANT_INFO, restaurantInfo)
                }
            }
    }

    private var _binding: BottomsheetRestaurantInfoBinding? = null
    private val binding get() = _binding!!
    private val restaurantInfo: RestaurantInfo by lazy {
        BundleCompat.getParcelable(requireArguments(), ARG_RESTAURANT_INFO, RestaurantInfo::class.java)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetRestaurantInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(binding.mvMap) {
            onCreate(null)
            getMapAsync(this@RestaurantInfoBottomSheet)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let {
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initData()
        initClickListener()
    }

    private fun initView() {
        with(binding) {
            tvTitle.text = restaurantInfo.nameKr
        }
    }

    private fun initData() {
        binding.restaurantOperatingTimes = restaurantInfo.etc?.operatingHours?.toRestaurantOperatingTimes() // TODO: RestaurantInfo단부터 DTO 대신 UiState 만들어 사용하기
    }

    private fun initClickListener() {
        binding.ivCloseButton.setOnClickListener { dismiss() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(requireContext())
        val position = LatLng(restaurantInfo.latitude ?: 0.0, restaurantInfo.longitude ?: 0.0)

        val markerRoot = LayoutInflater.from(context).inflate(R.layout.layout_map_marker, null)
        val markerText = markerRoot.findViewById<TextView>(R.id.text)
        markerText.text = restaurantInfo.address?.replace("서울 관악구 관악로 1", "") ?: "정보 없음"
        markerRoot.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        markerRoot.layout(0, 0, markerRoot.measuredWidth, markerRoot.measuredHeight)
        val bitmap = Bitmap.createBitmap(markerRoot.measuredWidth, markerRoot.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerRoot.draw(canvas)

        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.5f))
            addMarker(
                MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            )
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }
}
