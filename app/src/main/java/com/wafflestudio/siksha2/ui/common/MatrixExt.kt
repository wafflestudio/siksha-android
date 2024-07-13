package com.wafflestudio.siksha2.ui.common

import android.graphics.Matrix


val Matrix.scaleX get() = run {
    val values = FloatArray(9)
    getValues(values)
    values[Matrix.MSCALE_X]
}

val Matrix.translationX get() = run {
    val values = FloatArray(9)
    getValues(values)
    values[Matrix.MTRANS_X]
}

val Matrix.translationY get() = run {
    val values = FloatArray(9)
    getValues(values)
    values[Matrix.MTRANS_Y]
}
