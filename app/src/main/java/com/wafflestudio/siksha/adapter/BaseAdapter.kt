package com.wafflestudio.siksha.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : BaseViewHolder<T>>(
        private val getItems: () -> List<T>
) : RecyclerView.Adapter<VH>() {

    private var items: List<T> = getItems()

    fun refresh() {
        items = getItems()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
}