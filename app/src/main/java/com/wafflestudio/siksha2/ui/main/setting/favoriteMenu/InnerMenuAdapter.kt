package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemNotifyMenuBinding

class InnerMenuAdapter(
    private val onCheckedChanged: (Long, Boolean) -> Unit
) : ListAdapter<NotifyMenuUiModel, InnerMenuAdapter.MenuViewHolder>(diffCallback) {

    inner class MenuViewHolder(private val binding: ItemNotifyMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotifyMenuUiModel) {
            binding.menuTitleText.text = item.title

            val iconRes = if (item.isChecked) {
                R.drawable.ic_checkbox_checked
            } else {
                R.drawable.ic_checkbox_unchecked
            }
            binding.menuCheckbox.setImageResource(iconRes)

            binding.menuCheckbox.setOnClickListener {
                val newChecked = !item.isChecked
                onCheckedChanged(item.id, newChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotifyMenuBinding.inflate(inflater, parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
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
