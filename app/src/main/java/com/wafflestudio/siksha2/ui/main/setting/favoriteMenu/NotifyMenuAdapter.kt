package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemNotifyMenuBinding

class NotifyMenuAdapter(
    private val onCheckedChanged: (Long, Boolean) -> Unit
) : ListAdapter<NotifyMenuUiModel, NotifyMenuAdapter.NotifyMenuViewHolder>(diffCallback) {

    inner class NotifyMenuViewHolder(val binding: ItemNotifyMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotifyMenuUiModel) {
            binding.menuTitleText.text = item.title
            binding.menuCheckbox.isChecked = item.isChecked

            binding.menuCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChanged(item.id, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyMenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotifyMenuBinding.inflate(inflater, parent, false)
        return NotifyMenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotifyMenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<NotifyMenuUiModel>() {
            override fun areItemsTheSame(oldItem: NotifyMenuUiModel, newItem: NotifyMenuUiModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NotifyMenuUiModel, newItem: NotifyMenuUiModel) =
                oldItem == newItem
        }
    }
}

