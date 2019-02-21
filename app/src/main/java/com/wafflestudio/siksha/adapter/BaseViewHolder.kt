package com.wafflestudio.siksha.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(protected val view: View)
    : RecyclerView.ViewHolder(view) {

    protected val context: Context
        get() = view.context

    abstract fun bind(data: T)
}