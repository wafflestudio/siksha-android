package com.wafflestudio.siksha.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder<T>(protected val view: View) : RecyclerView.ViewHolder(view) {
    protected val context: Context
        get() = view.context

    abstract fun bind(data: T)
}