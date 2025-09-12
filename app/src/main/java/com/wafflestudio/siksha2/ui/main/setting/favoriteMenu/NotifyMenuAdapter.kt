package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemNotifyMenuBinding
import com.wafflestudio.siksha2.databinding.ItemNotifyMenuGroupBinding

class NotifyMenuAdapter(
    private val onCheckedChanged: (Long, Boolean) -> Unit
) : ListAdapter<NotifyMenuGroupUiModel, NotifyMenuAdapter.GroupViewHolder>(diffCallback) {

    inner class GroupViewHolder(val binding: ItemNotifyMenuGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val menuAdapter = InnerMenuAdapter(onCheckedChanged)

        init {
            binding.menuList.apply {
                adapter = menuAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(false)
            }
        }

        fun bind(item: NotifyMenuGroupUiModel) {
            binding.restaurantTitle.text = item.restaurantName

            if (!item.menus.isEmpty()) {
                menuAdapter.submitList(item.menus)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotifyMenuGroupBinding.inflate(inflater, parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<NotifyMenuGroupUiModel>() {
            override fun areItemsTheSame(oldItem: NotifyMenuGroupUiModel, newItem: NotifyMenuGroupUiModel) =
                oldItem.restaurantId == newItem.restaurantId

            override fun areContentsTheSame(oldItem: NotifyMenuGroupUiModel, newItem: NotifyMenuGroupUiModel) =
                oldItem == newItem
        }
    }
}



