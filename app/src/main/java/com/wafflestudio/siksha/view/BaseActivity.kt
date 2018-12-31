package com.wafflestudio.siksha.view

import android.content.Context
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    val context: Context
        get() = this
}