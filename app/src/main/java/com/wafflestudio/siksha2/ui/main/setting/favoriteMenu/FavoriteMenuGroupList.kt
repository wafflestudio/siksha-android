package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.network.dto.FavoriteMenuDto
import com.wafflestudio.siksha2.network.dto.FavoriteRestaurantDto
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun FavoriteRestaurantInfoRow(
    restaurantName: String?,
    isFavorite: Boolean,
    onToggleFavoriteRestaurant: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = restaurantName ?: "식당 이름 없음",
            color = SikshaTheme.colors.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Image(
            modifier = Modifier.size(20.dp)
                .clickable { onToggleFavoriteRestaurant() },
            painter = if (isFavorite) painterResource(R.drawable.ic_favorite_full) else painterResource(R.drawable.ic_favorite_empty),
            contentDescription = "식당 즐겨찾기"
        )
    }
}

@Composable
fun FavoriteMenuRoute(
    vm: FavoriteMenuViewModel,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    onToggleFavoriteRestaurant: (Long) -> Unit
) {
    val restaurants by vm.restaurants.collectAsState()
    val favoriteMap by vm.restaurantFavoriteMap.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(restaurants) { restaurant ->
            val isFavorite = favoriteMap[restaurant.id] == true

            RestaurantMenuFavorite(
                restaurant = restaurant,
                isFavorite = isFavorite,
                onToggleFavoriteRestaurant = {
                    vm.toggleRestaurantFavorite(restaurant.id)
                },
                onClickMenu = onClickMenu,
                onToggleLikeMenu = onToggleLikeMenu
            )
        }
    }
}

@Composable
fun RestaurantMenuFavorite(
    restaurant: FavoriteRestaurantDto,
    isFavorite: Boolean,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    onToggleFavoriteRestaurant: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(SikshaTheme.colors.White, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = SikshaTheme.colors.Gray200, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val restaurantName = restaurant.name_kr ?: "식당 이름 없음"

        val isNameTooLong = remember(restaurantName) {
            restaurantName.length > 14
        }

        if (isNameTooLong) {
            Column(modifier = Modifier.fillMaxWidth()) {
                FavoriteRestaurantInfoRow(
                    restaurantName = restaurantName,
                    isFavorite = isFavorite,
                    onToggleFavoriteRestaurant = onToggleFavoriteRestaurant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Price", fontSize = 12.sp, color = SikshaTheme.colors.Orange500)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Rate", fontSize = 12.sp, color = SikshaTheme.colors.Orange500)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Like", fontSize = 12.sp, color = SikshaTheme.colors.Orange500)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FavoriteRestaurantInfoRow(
                    restaurantName = restaurantName,
                    isFavorite = isFavorite,
                    onToggleFavoriteRestaurant = onToggleFavoriteRestaurant,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Price", fontSize = 12.sp, color = SikshaTheme.colors.Orange500)
                    Text("Rate", fontSize = 12.sp, color = SikshaTheme.colors.Orange500)
                    Text("Like", fontSize = 12.sp, color = SikshaTheme.colors.Orange500)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Divider(color = SikshaTheme.colors.Orange500, thickness = 1.5.dp)
        Spacer(modifier = Modifier.height(14.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            restaurant.menus.forEach { menu ->
                MenuRow(
                    menu = menu,
                    onClickMenu = { onClickMenu(menu.id) },
                    onToggleLikeMenu = { onToggleLikeMenu(menu.id, menu.is_liked) }
                )
            }
        }
    }
}

@Composable
fun MenuRow(
    menu: FavoriteMenuDto,
    onClickMenu: () -> Unit,
    onToggleLikeMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val wrappedName = remember(menu.name_kr) {
            menu.name_kr.chunked(15).joinToString("\n")
        }

        val formattedPrice = remember(menu.price) {
            val price = menu.price ?: 0
            "%,d".format(price)
        }

        val formattedRate = remember(menu.score) {
            menu.score?.let { String.format("%.1f", it) } ?: "-"
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .clickable { onClickMenu() },
            text = wrappedName,
            color = SikshaTheme.colors.Black,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            softWrap = true
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (menu.etc?.contains("No meat") == true) {
                Image(
                    painter = painterResource(R.drawable.ic_no_fork),
                    contentDescription = "No meat"
                )
            }
            Text(
                text = formattedPrice,
                color = SikshaTheme.colors.Black,
                fontSize = 14.sp
            )
            Box(
                modifier = Modifier.width(22.95.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedRate,
                    color = SikshaTheme.colors.Black,
                    fontSize = 14.sp
                )
            }
            Image(
                modifier = Modifier.size(20.dp).clickable { onToggleLikeMenu() },
                painter = painterResource(
                    if (menu.is_liked) {
                        R.drawable.ic_heart_filled
                    } else {
                        R.drawable.ic_heart_outline
                    }
                ),
                contentDescription = "메뉴 찜하기"
            )
        }
    }
}
