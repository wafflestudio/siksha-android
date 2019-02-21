package com.wafflestudio.siksha.view

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

class MySwipeRefreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout {
    private var mScrollingView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canChildScrollUp(): Boolean = mScrollingView?.let { it.canScrollVertically(-1) }
            ?: false

    fun setScrollingView(scrollingView: View) {
        mScrollingView = scrollingView
    }
}
