package com.wafflestudio.siksha2.ui.main.setting.reorder

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.settings.ReorderRestaurantNameCard
import com.wafflestudio.siksha2.databinding.ItemSettingRestaurantReorderBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.utils.getInflater
import com.woxthebox.draglistview.DragItemAdapter

class ReorderItemAdapter() : DragItemAdapter<Pair<Long, String>, ReorderItemAdapter.ViewHolder>() {
    fun submitList(list: List<Pair<Long, String>>) {
        itemList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSettingRestaurantReorderBinding.inflate(parent.getInflater(), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.restaurantName.setContent {
            SikshaTheme {
                ReorderRestaurantNameCard(mItemList[position].second)
            }
        }

        val nameBg = when (position) {
            0 -> R.drawable.frame_top_left_corner_radius_12
            itemCount - 1 -> R.drawable.frame_bottom_left_corner_radius_12
            else -> R.color.gray_50
        }
        val handleBg = when (position) {
            0 -> R.drawable.frame_top_right_corner_radius_12
            itemCount - 1 -> R.drawable.frame_bottom_right_corner_radius_12
            else -> R.color.background_secondary
        }

        holder.binding.restaurantNameBackground.background = ContextCompat.getDrawable(holder.itemView.context, nameBg)
        holder.binding.reorderHandleBackground.background = ContextCompat.getDrawable(holder.itemView.context, handleBg)
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first
    }

    class ViewHolder(val binding: ItemSettingRestaurantReorderBinding) :
        DragItemAdapter.ViewHolder(binding.root, R.id.reorder_handle, false)
}
