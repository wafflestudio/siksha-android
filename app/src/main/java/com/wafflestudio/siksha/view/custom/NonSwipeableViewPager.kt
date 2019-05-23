package com.wafflestudio.siksha.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

@Suppress("SpellCheckingInspection")
class NonSwipeableViewPager : ViewPager {

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

  override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent?): Boolean = false

  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false
}