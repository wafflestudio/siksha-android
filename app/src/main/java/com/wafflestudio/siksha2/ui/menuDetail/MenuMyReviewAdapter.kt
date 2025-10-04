package com.wafflestudio.siksha2.ui.menuDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemMyReviewRestaurantBinding
import com.wafflestudio.siksha2.network.dto.ReviewRestaurant

class MenuMyReviewAdapter :
    PagingDataAdapter<ReviewRestaurant, MenuMyReviewAdapter.RestaurantViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReviewRestaurant>() {
            override fun areItemsTheSame(oldItem: ReviewRestaurant, newItem: ReviewRestaurant): Boolean {
                return oldItem.restaurantId == newItem.restaurantId
            }

            override fun areContentsTheSame(oldItem: ReviewRestaurant, newItem: ReviewRestaurant): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class RestaurantViewHolder(private val binding: ItemMyReviewRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false

        fun bind(item: ReviewRestaurant) {
            binding.restaurantName.text = item.nameKr
            binding.arrowIcon.rotation = if (isExpanded) 180f else 0f

            // 리뷰 어댑터 (Nested RecyclerView)
            binding.reviewRecycler.layoutManager = LinearLayoutManager(binding.root.context)
            val reviewAdapter = MenuMyReviewChildAdapter()
            binding.reviewRecycler.adapter = reviewAdapter

            if (binding.reviewRecycler.itemDecorationCount == 0) {
                val divider = DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL)
                val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.divider_gray_line)
                divider.setDrawable(drawable!!)
                binding.reviewRecycler.addItemDecoration(divider)
            }

            reviewAdapter.submitList(item.reviews)

            // 펼치기/접기
            binding.root.setOnClickListener {
                isExpanded = !isExpanded
                binding.arrowIcon.animate().rotation(if (isExpanded) 180f else 0f).start()
                binding.reviewRecycler.isVisible = isExpanded
                binding.topDivider.isVisible = isExpanded
            }

            // 초기에는 접혀있게
            binding.reviewRecycler.isVisible = isExpanded
        }
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemMyReviewRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }
}
