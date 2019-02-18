package com.wafflestudio.siksha.view

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

class MySwipeRefreshLayout : SwipeRefreshLayout {
    private var mScrollingView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canChildScrollUp(): Boolean = mScrollingView?.let { it.canScrollVertically(-1) }
            ?: false

    fun setScrollingView(scrollingView: View) {
        mScrollingView = scrollingView
    }
}
