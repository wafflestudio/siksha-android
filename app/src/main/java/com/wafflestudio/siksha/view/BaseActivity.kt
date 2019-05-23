package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.android.AndroidInjection

abstract class BaseActivity : AppCompatActivity() {

  val context: Context
    get() = this

  fun color(@ColorRes id: Int) = ContextCompat.getColor(this, id)

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
  }
}