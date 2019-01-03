package com.wafflestudio.siksha.adapter

import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<T, VH : BaseViewHolder<T>>(
        private val items: List<T>
) : RecyclerView.Adapter<VH>() {
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }
}