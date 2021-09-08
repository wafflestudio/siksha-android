package com.wafflestudio.siksha2.ui.main.restaurant

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.CalendarSelectView
import com.wafflestudio.siksha2.databinding.FragmentDailyRestaurantBinding
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.ui.main.MainFragmentDirections
import com.wafflestudio.siksha2.ui.restaurant_info.RestaurantInfoDialogFragment
import com.wafflestudio.siksha2.utils.toPrettyString
import com.wafflestudio.siksha2.utils.visibleOrGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class DailyRestaurantFragment : Fragment() {
    private val vm: DailyRestaurantViewModel by viewModels()

    private lateinit var binding: FragmentDailyRestaurantBinding
    private lateinit var adapter: MenuGroupAdapter

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
        vm.startRefreshingData()
        vm.checkFavoriteRestaurantExists()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.OnGestureListener {
                override fun onDown(p0: MotionEvent?): Boolean { return false }
                override fun onShowPress(p0: MotionEvent?) {}
                override fun onSingleTapUp(p0: MotionEvent?): Boolean { return false }
                override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean { return false }
                override fun onLongPress(p0: MotionEvent?) {}
                override fun onFling(
                    p0: MotionEvent?,
                    p1: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    if (Math.abs(velocityY) > Math.abs(velocityX)) return false

                    if (velocityX > 2000) {
                        when (vm.mealsOfDayFilter.value) {
                            MealsOfDay.BR -> {
                                vm.addDateOffset(-1L)
                                vm.setMealsOfDayFilter(MealsOfDay.DN)
                            }
                            MealsOfDay.LU -> vm.setMealsOfDayFilter(MealsOfDay.BR)
                            MealsOfDay.DN -> vm.setMealsOfDayFilter(MealsOfDay.LU)
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
                        }

                        return true
                    }

                    return false
                }
            }
        )

        adapter = MenuGroupAdapter(
            onMenuGroupInfoClickListener = {
                lifecycleScope.launch {
                    vm.getRestaurantInfo(it)?.let {
                        // TODO: BottomSheetController 따로 만들어 Inject 받아쓰기
                        val bottomSheet = RestaurantInfoDialogFragment.newInstance(it)
                        bottomSheet.showNow(parentFragmentManager, "restaurant_info_${it.id}")
                    }
                }
            },
            onMenuGroupToggleFavoriteClickListener = {
                vm.toggleFavorite(it)
            },
            onMenuItemClickListener = {
                val action =
                    MainFragmentDirections.actionMainFragmentToMenuDetailFragment(
                        it,
                        vm.dateFilter.value == LocalDate.now()
                    )
                findNavController().navigate(action)
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
            it.adapter = adapter
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

//        binding.menuGroupList.setOnTouchListener { _, p1 ->
//            gestureDetector.onTouchEvent(p1)
//            true
//        }
//
//        binding.emptyText.setOnTouchListener { _, p1 ->
//            gestureDetector.onTouchEvent(p1)
//            true
//        }

        vm.favoriteRestaurantExists.observe(viewLifecycleOwner) {
            if (isFavorite) {
                binding.emptyFavorite.root.visibleOrGone(it.not())
                binding.content.visibleOrGone(it)
            }
        }

        lifecycleScope.launch {
            vm.getFilteredMenuGroups(isFavorite)
                .collect {
                    binding.menuGroupList.visibleOrGone(it.isNotEmpty())
                    binding.emptyText.visibleOrGone(it.isEmpty())
                    adapter.submitList(it)
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
                    binding.breakfastText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_700))
                    binding.lunchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_500))
                    binding.dinnerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_500))
                }
                MealsOfDay.LU -> {
                    binding.breakfastText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_500))
                    binding.lunchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_700))
                    binding.dinnerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_500))
                }
                MealsOfDay.DN -> {
                    binding.breakfastText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_500))
                    binding.lunchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_500))
                    binding.dinnerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_700))
                }
            }
            binding.tabBreakfast.isSelected = mealsOfDay == MealsOfDay.BR
            binding.tabLunch.isSelected = mealsOfDay == MealsOfDay.LU
            binding.tabDinner.isSelected = mealsOfDay == MealsOfDay.DN
        }

        vm.isCalendarVisible.observe(viewLifecycleOwner) { visibility ->
            binding.calendarLayout.visibleOrGone(visibility)
            binding.dateAfter.visibleOrGone(!visibility)
            binding.dateBefore.visibleOrGone(!visibility)
        }

        binding.breakfastLayout.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.BR) }
        binding.lunchLayout.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.LU) }
        binding.dinnerLayout.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.DN) }

        binding.dateBefore.setOnClickListener { vm.addDateOffset(-1L) }
        binding.dateAfter.setOnClickListener { vm.addDateOffset(1L) }
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
