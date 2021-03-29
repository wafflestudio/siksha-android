package com.wafflestudio.siksha2.ui.main.restaurant

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

@AndroidEntryPoint
class DailyRestaurantFragment : Fragment() {
    private val vm: DailyRestaurantViewModel by viewModels()

    private lateinit var binding: FragmentDailyRestaurantBinding
    private lateinit var adapter: MenuGroupAdapter

    // 즐겨찾기 식당 탭과 일반 식당 탭이 다른 프래그먼트로 분리하기엔 중복이 많아서 플래그로 넘겨받고 관리.
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFavorite = it.getBoolean(IS_FAVORITE)
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

        binding.menuGroupList.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(context)
        }

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

        vm.dateFilter.observe(viewLifecycleOwner) { date ->
            binding.dateBefore.text = date.minusDays(1).toPrettyString()
            ObjectAnimator.ofFloat(binding.dateBefore, View.ALPHA, 0f, 1f)
                .apply { duration = 250 }.start()
            binding.dateCurrent.text = date.toPrettyString()
            ObjectAnimator.ofFloat(binding.dateCurrent, View.ALPHA, 0f, 1f)
                .apply { duration = 250 }.start()
            binding.dateAfter.text = date.plusDays(1).toPrettyString()
            ObjectAnimator.ofFloat(binding.dateAfter, View.ALPHA, 0f, 1f)
                .apply { duration = 250 }.start()
        }

        vm.mealsOfDayFilter.observe(viewLifecycleOwner) { mealsOfDay ->
            binding.tabBreakfast.isSelected = mealsOfDay == MealsOfDay.BR
            binding.tabLunch.isSelected = mealsOfDay == MealsOfDay.LU
            binding.tabDinner.isSelected = mealsOfDay == MealsOfDay.DN
        }

        binding.tabBreakfast.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.BR) }
        binding.tabLunch.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.LU) }
        binding.tabDinner.setOnClickListener { vm.setMealsOfDayFilter(MealsOfDay.DN) }

        binding.dateBefore.setOnClickListener { vm.addDateOffset(-1L) }
        binding.dateAfter.setOnClickListener { vm.addDateOffset(1L) }
    }

    companion object {
        const val IS_FAVORITE = "is_favorite"

        @JvmStatic
        fun newInstance(isFavorite: Boolean) =
            DailyRestaurantFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_FAVORITE, isFavorite)
                }
            }
    }
}
