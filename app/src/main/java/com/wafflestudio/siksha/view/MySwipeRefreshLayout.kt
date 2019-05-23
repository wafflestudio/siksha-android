package com.wafflestudio.siksha.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MySwipeRefreshLayout : SwipeRefreshLayout {

  private var mScrollingView: View? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  override fun canChildScrollUp(): Boolean = mScrollingView?.canScrollVertically(-1) ?: false

  fun setScrollingView(scrollingView: View) {
    mScrollingView = scrollingView
  }
}
