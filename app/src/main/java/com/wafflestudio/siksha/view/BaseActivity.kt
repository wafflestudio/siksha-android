package com.wafflestudio.siksha.view

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import dagger.android.DaggerActivity

abstract class BaseActivity : DaggerActivity() {

  val context: Context
    get() = this

  fun color(@ColorRes id: Int) = ContextCompat.getColor(this, id)
}