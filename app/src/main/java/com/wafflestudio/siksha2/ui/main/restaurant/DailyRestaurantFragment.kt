package com.wafflestudio.siksha2.ui.main.restaurant

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.CalendarSelectView
import com.wafflestudio.siksha2.databinding.FragmentDailyRestaurantBinding
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import com.wafflestudio.siksha2.ui.restaurantInfo.RestaurantInfoBottomSheet
import com.wafflestudio.siksha2.utils.KakaoLinkHelper
import com.wafflestudio.siksha2.utils.toPrettyString
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import kotlin.math.abs

@AndroidEntryPoint
class DailyRestaurantFragment : Fragment() {
    private val vm: DailyRestaurantViewModel by activityViewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

//    private val locationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        result ->
//
//    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // TODO: placeholder 삭제
            showToast("위치 권한이 허용되었습니다.")
        } else {
            showToast("위치 기반 필터링 이용을 위해 위치 권한을 허용해 주세요.")
        }
    }

    private lateinit var binding: FragmentDailyRestaurantBinding
    private lateinit var menuGroupAdapter: MenuGroupAdapter
    private lateinit var gestureDetector: GestureDetector

    // 즐겨찾기 식당 탭과 일반 식당 탭이 다른 프래그먼트로 분리하기엔 중복이 많아서 플래그로 넘겨받고 관리.
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFavorite = it.getBoolean(IS_FAVORITE)
        }

        when (LocalTime.now().hour) {
            in 0..9 -> vm.setMealsOfDayFilter(MealsOfDay.BR)
            in 9..13 -> vm.setMealsOfDayFilter(MealsOfDay.LU)
            else -> vm.setMealsOfDayFilter(MealsOfDay.DN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.checkFavoriteRestaurantExists()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.OnGestureListener {
                override fun onDown(p0: MotionEvent): Boolean {
                    return false
                }

                override fun onShowPress(p0: MotionEvent) {}
                override fun onSingleTapUp(p0: MotionEvent): Boolean {
                    return false
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    return false
                }

                override fun onLongPress(p0: MotionEvent) {}
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (abs(velocityY) > abs(velocityX)) return false

                    if (velocityX > 2000) {
                        when (vm.mealsOfDayFilter.value) {
                            MealsOfDay.BR -> {
                                vm.addDateOffset(-1L)
                                vm.setMealsOfDayFilter(MealsOfDay.DN)
                            }

                            MealsOfDay.LU -> vm.setMealsOfDayFilter(MealsOfDay.BR)
                            MealsOfDay.DN -> vm.setMealsOfDayFilter(MealsOfDay.LU)
                            else -> {}
                        }

                        return true
                    }

                    if (velocityX < -2000) {
                        when (vm.mealsOfDayFilter.value) {
                            MealsOfDay.BR -> vm.setMealsOfDayFilter(MealsOfDay.LU)
                            MealsOfDay.LU -> vm.setMealsOfDayFilter(MealsOfDay.DN)
                            MealsOfDay.DN -> {
                                vm.addDateOffset(1L)
                                vm.setMealsOfDayFilter(MealsOfDay.BR)
                            }

                            else -> {}
                        }

                        return true
                    }

                    return false
                }
            }
        )

        menuGroupAdapter = MenuGroupAdapter(
            onMenuGroupInfoClickListener = {
                lifecycleScope.launch {
                    vm.getRestaurantInfo(it)?.let {
                        // TODO: BottomSheetController 따로 만들어 Inject 받아쓰기
                        val bottomSheet = RestaurantInfoBottomSheet.newInstance(it)
                        bottomSheet.showNow(parentFragmentManager, "restaurant_info_${it.id}")
                    }
                }
            },
            onMenuGroupToggleFavoriteClickListener = {
                vm.toggleRestaurantFavorite(it)
            },
            onMenuItemToggleLikeClickListener = { menuId, isCurrentlyLiked ->
                viewLifecycleOwner.lifecycleScope.launch {
                    when (val response = vm.toggleMenuLike(menuId, isCurrentlyLiked)) {
                        is NetworkResult.Success -> { }
                        is NetworkResult.Failure -> showToast(response.message)
                        is NetworkResult.NetworkError -> showToast(getString(R.string.common_network_error))
                        else -> showToast(getString(R.string.common_unknown_error))
                    }
                }
            },
            onMenuItemClickListener = {
                val action =
                    MainFragmentDirections.actionMainFragmentToMenuDetailFragment(
                        it,
                        vm.dateFilter.value == LocalDate.now()
                    )
                findNavController().navigate(action)
            },
            onMenuGroupShareClickListener = { menuGroupId ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val menuGroup = vm.getMenuGroupById(menuGroupId)
                    val shareDate = vm.dateFilter.value ?: LocalDate.now()

                    if (menuGroup != null) {
                        val menuData = menuGroup.menus.take(5).map {
                            (it.nameKr ?: "메뉴 이름 없음") to (it.price?.toString() ?: "가격 없음")
                        }
                        KakaoLinkHelper.shareMenuWithTemplate(
                            requireContext(),
                            menuData,
                            menuGroup.nameKr ?: "식당 이름 없음",
                            shareDate
                        )
                    } else {
                        showToast("해당 메뉴 그룹을 찾을 수 없습니다.")
                    }
                }
            }
        )

        binding.calendarSelectView.updateDate(LocalDate.now())
        binding.calendarSelectView.setDateChangeListener(
            object : CalendarSelectView.OnDateChangeListener {
                override fun onChange(date: LocalDate) {
                    vm.setDateFilter(date)
                    vm.setCalendarVisibility(false)
                }
            }
        )

        binding.menuGroupList.also {
            it.adapter = menuGroupAdapter
            it.layoutManager = LinearLayoutManager(context)
        }

        binding.menuGroupList.setOnTouchListener { _, ev ->
            gestureDetector.onTouchEvent(ev)
            false
        }

        binding.emptyText.setOnTouchListener { _, ev ->
            gestureDetector.onTouchEvent(ev)
            true
        }

        vm.favoriteRestaurantExists.observe(viewLifecycleOwner) {
            if (isFavorite) {
                binding.emptyFavorite.root.setVisibleOrGone(it.not())
                binding.content.setVisibleOrGone(it)
            }
        }
        binding.menuGroupList.itemAnimator = null

        viewLifecycleOwner.lifecycleScope.launch {
            vm.getFilteredMenuGroups(isFavorite)
                .collect {
                    binding.menuGroupList.setVisibleOrGone(it.isNotEmpty())
                    binding.emptyText.setVisibleOrGone(it.isEmpty())
                    menuGroupAdapter.submitList(it)
                }
        }

        binding.layoutSelectCalendar.setOnClickListener {
            vm.toggleCalendarVisibility()
        }

        binding.blank.setOnClickListener {
            vm.setCalendarVisibility(false)
        }

        vm.dateFilter.observe(viewLifecycleOwner) { date ->
            binding.calendarSelectView.setSelectedDate(date)
            ObjectAnimator.ofFloat(binding.dateBefore, View.ALPHA, 0f, 1f)
                .apply { duration = 250 }.start()
            binding.dateCurrent.text = date.toPrettyString()
            ObjectAnimator.ofFloat(binding.dateCurrent, View.ALPHA, 0f, 1f)
                .apply { duration = 250 }.start()
            ObjectAnimator.ofFloat(binding.dateAfter, View.ALPHA, 0f, 1f)
                .apply { duration = 250 }.start()

            binding.calendarSelectView.updateDateWithoutListener(date)
        }

        vm.mealsOfDayFilter.observe(viewLifecycleOwner) { mealsOfDay ->
            when (mealsOfDay) {
                MealsOfDay.BR -> {
                    binding.breakfastText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange_main
                        )
                    )
                    binding.lunchText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_500
                        )
                    )
                    binding.dinnerText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_500
                        )
                    )
                }

                MealsOfDay.LU -> {
                    binding.breakfastText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_500
                        )
                    )
                    binding.lunchText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange_main
                        )
                    )
                    binding.dinnerText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_500
                        )
                    )
                }

                MealsOfDay.DN -> {
                    binding.breakfastText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_500
                        )
                    )
                    binding.lunchText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray_500
                        )
                    )
                    binding.dinnerText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange_main
                        )
                    )
                }
            }
            binding.tabBreakfast.isSelected = mealsOfDay == MealsOfDay.BR
            binding.tabLunch.isSelected = mealsOfDay == MealsOfDay.LU
            binding.tabDinner.isSelected = mealsOfDay == MealsOfDay.DN
        }

        vm.isCalendarVisible.observe(viewLifecycleOwner) { visibility ->
            binding.calendarLayout.setVisibleOrGone(visibility)
            binding.dateAfter.setVisibleOrGone(!visibility)
            binding.dateBefore.setVisibleOrGone(!visibility)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.menuFilterCondition.collect { condition ->
                binding.filterDistance.setFilter(
                    if (condition.distance >= 1000) "거리" else "${condition.distance.toInt()}m 이내",
                    condition.distance >= 1000f
                )

                binding.filterPrice.setFilter(
                    if (condition.minPrice > 3000f || condition.maxPrice < 10000f) {
                        val minPriceText = if (condition.minPrice <= 3000) "3,000원 이하" else "${String.format(Locale.getDefault(), "%,d", condition.minPrice.toInt())}원"
                        val maxPriceText = if (condition.maxPrice >= 10000) "10,000원 이상" else "${String.format(Locale.getDefault(), "%,d", condition.maxPrice.toInt())}원"
                        "$minPriceText ~ $maxPriceText"
                    } else {
                        "가격"
                    },
                    condition.maxPrice == 10000f && condition.minPrice == 3000f
                )

                binding.filterOpen.showCheck(condition.isOpen)
                binding.filterReview.showCheck(condition.hasReview)

                binding.filterRating.setFilter(
                    if(condition.minRating != 0f){
                        condition.minRating.let { rating -> "평점 $rating 이상" }
                    }else{
                        "최소 평점"
                    },
                    condition.minRating == 0f
                )
                binding.filterCategory.setFilter(
                    condition.categories.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "카테고리",
                    condition.categories.isEmpty()
                )
            }
        }

        binding.breakfastLayout.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.BR) }
        binding.lunchLayout.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.LU) }
        binding.dinnerLayout.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.DN) }

        binding.dateBefore.setOnClickListener { vm.addDateOffset(-1L) }
        binding.dateAfter.setOnClickListener { vm.addDateOffset(1L) }

        binding.menuFilter.setOnClickListener {
            val filterDialog = FilterDialogFragment(FilterMode.FULL)
            filterDialog.show(parentFragmentManager, "FilterDialog")
        }

        binding.filterDistance.setOnClickListener {
            val filterDialog = FilterDialogFragment(FilterMode.DISTANCE)
            filterDialog.show(parentFragmentManager, "FilterDialog")
        }

        binding.filterPrice.setOnClickListener {
            val filterDialog = FilterDialogFragment(FilterMode.PRICE)
            filterDialog.show(parentFragmentManager, "FilterDialog")
        }

        binding.filterOpen.setOnClickListener {
            vm.toggleOpenFilter()
        }

        binding.filterReview.setOnClickListener {
            vm.toggleReviewFilter()
        }

        binding.filterRating.setOnClickListener {
            val filterDialog = FilterDialogFragment(FilterMode.RATING)
            filterDialog.show(parentFragmentManager, "FilterDialog")
        }

        binding.filterCategory.setOnClickListener {
            val filterDialog = FilterDialogFragment(FilterMode.CATEGORY)
            filterDialog.show(parentFragmentManager, "FilterDialog")
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.Builder(5000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                vm.updateLocation(location)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission {
                // TODO: placeholder 삭제
                showToast("위치 권한이 허용되었습니다.")
            }
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, requireActivity().mainLooper)
        }
    }

    private fun requestPermission(onGranted: () -> Unit) {
        // TODO: SDK 버전에 따른 처리 필요한지 확인
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    companion object {
        const val IS_FAVORITE = "is_favorite"
        private const val SAVED_INSTANCE_MEALS_OF_DAY = "meals_of_day"

        @JvmStatic
        fun newInstance(isFavorite: Boolean) =
            DailyRestaurantFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_FAVORITE, isFavorite)
                }
            }
    }
}
