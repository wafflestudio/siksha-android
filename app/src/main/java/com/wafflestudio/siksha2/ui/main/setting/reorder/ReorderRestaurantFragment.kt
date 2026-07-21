package com.wafflestudio.siksha2.ui.main.setting.reorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.FragmentReorderRestaurantBinding
import com.wafflestudio.siksha2.models.RestaurantOrder
import com.wafflestudio.siksha2.ui.main.setting.SettingViewModel
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.woxthebox.draglistview.DragListView
import kotlinx.coroutines.launch
import java.util.*

class ReorderRestaurantFragment : Fragment() {
    private val vm: SettingViewModel by activityViewModels()
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

        reorderItemAdapter = ReorderItemAdapter()

        setupRecyclerView()

        binding.orderList.setDragListListener(
            object : DragListView.DragListListenerAdapter() {
                override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                    val item = order.removeAt(fromPosition)
                    order.add(toPosition, item)
                    if (args.orderArg) {
                        vm.updateFavoriteOrder(RestaurantOrder(order))
                    } else {
                        vm.updateOrder(RestaurantOrder(order))
                    }
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
        binding.orderList.apply {
            setLayoutManager(LinearLayoutManager(context))
            setAdapter(reorderItemAdapter, false)
            setCanDragHorizontally(false)
        }

        lifecycleScope.launch {
            val list =
                if (args.orderArg) {
                    vm.getOrderedFavoriteRestaurants()
                } else {
                    vm.getOrderedAllRestaurants()
                }
            order.clear()
            order.addAll(list.map { it.id })
            binding.emptyOrder.setVisibleOrGone(list.isEmpty())
            binding.orderList.setVisibleOrGone(list.isNotEmpty())
            reorderItemAdapter.submitList(list.map { Pair(it.id, it.nameKr ?: "") })
        }
    }
}
