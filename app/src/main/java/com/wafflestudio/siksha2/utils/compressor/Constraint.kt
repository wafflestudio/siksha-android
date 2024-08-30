package com.wafflestudio.siksha2.utils.compressor

import java.io.File

interface Constraint {
    fun isSatisfied(imageFile: File): Boolean

    fun satisfy(imageFile: File): File
}
