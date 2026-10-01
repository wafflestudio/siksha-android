package com.wafflestudio.siksha2.ui.main.setting.reorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentReorderRestaurantBinding
import com.wafflestudio.siksha2.models.RestaurantOrder
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import com.woxthebox.draglistview.DragListView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ReorderRestaurantFragment : Fragment() {
    private val vm: ReorderRestaurantViewModel by viewModels()
    private lateinit var binding: FragmentReorderRestaurantBinding
    private lateinit var reorderItemAdapter: ReorderItemAdapter

    private val args: ReorderRestaurantFragmentArgs by navArgs()

    private val order = LinkedList<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReorderRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reorderItemAdapter =
            ReorderItemAdapter(
                onFavoriteClick = vm::toggleFavorite,
                onVisibleClick = vm::toggleVisible
            )

        setupRecyclerView()
        observePersonalRestaurants()
        vm.loadRestaurants(onlyFavorites = args.orderArg)

        binding.orderList.setDragListListener(
            object : DragListView.DragListListenerAdapter() {
                override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                    val previousOrder = order.toList()
                    val item = order.removeAt(fromPosition)
                    order.add(toPosition, item)
                    vm.updateOrder(
                        order = RestaurantOrder(order.toList()),
                        previousOrder = RestaurantOrder(previousOrder),
                        favoriteOnly = args.orderArg
                    )
                }
            }
        )

        binding.title.text =
            getString(
                if (args.orderArg) {
                    R.string.setting_reorder_favorite_title
                } else {
                    R.string.setting_reorder_restaurant_title
                }
            )

        binding.title.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.orderList.recyclerView.isVerticalScrollBarEnabled = true
        binding.orderList.recyclerView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_INSET
        binding.orderList.recyclerView.addItemDecoration(
            RestaurantListCardDecoration(requireContext())
        )
        binding.orderList.apply {
            setLayoutManager(LinearLayoutManager(context))
            setAdapter(reorderItemAdapter, false)
            setCanDragHorizontally(false)
        }
    }

    private fun observePersonalRestaurants() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.uiState.collect { state ->
                        binding.onLoadingContainer.root.setVisibleOrGone(
                            state is ReorderRestaurantUiState.Loading
                        )
                        binding.onErrorContainer.root.setVisibleOrGone(
                            state is ReorderRestaurantUiState.Failure
                        )

                        when (state) {
                            is ReorderRestaurantUiState.Success -> renderRestaurants(state.restaurants)
                            is ReorderRestaurantUiState.Failure -> {
                                binding.onErrorContainer.textView2.text = state.message
                                binding.emptyOrder.setVisibleOrGone(false)
                                binding.orderList.setVisibleOrGone(false)
                            }
                            ReorderRestaurantUiState.Idle,
                            ReorderRestaurantUiState.Loading -> {
                                binding.emptyOrder.setVisibleOrGone(false)
                                binding.orderList.setVisibleOrGone(false)
                            }
                        }
                    }
                }
                launch {
                    vm.errorEvents.collect { message ->
                        showToast(message)
                    }
                }
            }
        }
    }

    private fun renderRestaurants(restaurants: List<RestaurantInfo>) {
        order.clear()
        order.addAll(restaurants.map { it.id })
        binding.emptyOrder.setVisibleOrGone(restaurants.isEmpty())
        binding.orderList.setVisibleOrGone(restaurants.isNotEmpty())

        reorderItemAdapter.submitList(
            restaurants.map { restaurant ->
                ReorderRestaurant(
                    id = restaurant.id,
                    name = restaurant.nameKr ?: restaurant.nameEn.orEmpty(),
                    isFavorite = restaurant.isFavorite,
                    visible = restaurant.visible
                )
            }
        )
    }
}
