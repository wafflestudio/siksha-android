package com.wafflestudio.siksha2.ui.main.setting.reorder

import android.view.ViewGroup
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemSettingRestaurantReorderBinding
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
        holder.binding.restaurantName.text = mItemList[position].second
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first
    }

    class ViewHolder(val binding: ItemSettingRestaurantReorderBinding) :
        DragItemAdapter.ViewHolder(binding.root, R.id.reorder_handle, false)
}
