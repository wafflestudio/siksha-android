package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

data class NotifyMenuUiModel(
    val id: Long,
    val title: String,
    val alarm: Boolean,
    var isChecked: Boolean
)

data class NotifyMenuGroupUiModel(
    val restaurantId: Long,
    val restaurantName: String,
    val menus: List<NotifyMenuUiModel>
)
