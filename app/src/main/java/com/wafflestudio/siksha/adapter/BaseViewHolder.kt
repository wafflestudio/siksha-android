package com.wafflestudio.siksha.adapter

import android.content.Context
import android.view.View

abstract class BaseViewHolder<T>(protected val view: View)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{

    protected val context: Context
        get() = view.context

    abstract fun bind(data: T)
}