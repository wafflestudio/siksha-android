package com.wafflestudio.siksha.view

import android.content.Context
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {
    val context: Context
        get() = this

    fun color(@ColorRes id: Int) = ContextCompat.getColor(this, id)
}