package com.wafflestudio.siksha.view

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    val context: Context
        get() = this

    fun color(@ColorRes id: Int) = ContextCompat.getColor(this, id)
}