package com.wafflestudio.siksha2.ui.main.setting.reorder

data class ReorderRestaurant(
    val id: Long,
    val name: String,
    var isFavorite: Boolean,
    var visible: Boolean
)
